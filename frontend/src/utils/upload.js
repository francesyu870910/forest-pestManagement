import { ElMessage } from 'element-plus'

/**
 * 文件上传工具类
 */
export class FileUploadManager {
  constructor(options = {}) {
    this.options = {
      maxSize: 10 * 1024 * 1024, // 10MB
      allowedTypes: ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
      maxWidth: 1920,
      maxHeight: 1080,
      quality: 0.8,
      ...options
    }
  }

  // 验证文件
  validateFile(file) {
    const errors = []

    // 检查文件大小
    if (file.size > this.options.maxSize) {
      errors.push(`文件大小不能超过 ${this.formatFileSize(this.options.maxSize)}`)
    }

    // 检查文件类型
    if (!this.options.allowedTypes.includes(file.type)) {
      errors.push(`不支持的文件类型，支持: ${this.options.allowedTypes.join(', ')}`)
    }

    return {
      valid: errors.length === 0,
      errors
    }
  }

  // 压缩图片
  async compressImage(file) {
    return new Promise((resolve, reject) => {
      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')
      const img = new Image()

      img.onload = () => {
        // 计算压缩后的尺寸
        const { width, height } = this.calculateDimensions(
          img.width,
          img.height,
          this.options.maxWidth,
          this.options.maxHeight
        )

        canvas.width = width
        canvas.height = height

        // 绘制压缩后的图片
        ctx.drawImage(img, 0, 0, width, height)

        // 转换为Blob
        canvas.toBlob(
          (blob) => {
            if (blob) {
              // 创建新的File对象
              const compressedFile = new File([blob], file.name, {
                type: file.type,
                lastModified: Date.now()
              })
              resolve(compressedFile)
            } else {
              reject(new Error('图片压缩失败'))
            }
          },
          file.type,
          this.options.quality
        )
      }

      img.onerror = () => {
        reject(new Error('图片加载失败'))
      }

      img.src = URL.createObjectURL(file)
    })
  }

  // 计算压缩尺寸
  calculateDimensions(originalWidth, originalHeight, maxWidth, maxHeight) {
    let { width, height } = { width: originalWidth, height: originalHeight }

    // 如果图片尺寸超过限制，按比例缩放
    if (width > maxWidth || height > maxHeight) {
      const ratio = Math.min(maxWidth / width, maxHeight / height)
      width = Math.floor(width * ratio)
      height = Math.floor(height * ratio)
    }

    return { width, height }
  }

  // 生成预览URL
  generatePreviewUrl(file) {
    return URL.createObjectURL(file)
  }

  // 释放预览URL
  revokePreviewUrl(url) {
    URL.revokeObjectURL(url)
  }

  // 格式化文件大小
  formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes'

    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))

    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  // 批量处理文件
  async processFiles(files) {
    const results = []

    for (const file of files) {
      try {
        // 验证文件
        const validation = this.validateFile(file)
        if (!validation.valid) {
          results.push({
            file,
            success: false,
            errors: validation.errors
          })
          continue
        }

        // 压缩图片（如果是图片文件）
        let processedFile = file
        if (file.type.startsWith('image/')) {
          processedFile = await this.compressImage(file)
        }

        results.push({
          file: processedFile,
          originalFile: file,
          success: true,
          previewUrl: this.generatePreviewUrl(processedFile)
        })
      } catch (error) {
        results.push({
          file,
          success: false,
          errors: [error.message]
        })
      }
    }

    return results
  }
}

/**
 * 上传进度管理器
 */
export class UploadProgressManager {
  constructor() {
    this.uploads = new Map()
  }

  // 创建上传任务
  createUpload(id, file, options = {}) {
    const upload = {
      id,
      file,
      status: 'pending', // pending, uploading, success, error, cancelled
      progress: 0,
      speed: 0,
      timeRemaining: 0,
      startTime: null,
      endTime: null,
      error: null,
      ...options
    }

    this.uploads.set(id, upload)
    return upload
  }

  // 更新上传进度
  updateProgress(id, progress, loaded, total) {
    const upload = this.uploads.get(id)
    if (!upload) return

    const now = Date.now()
    const elapsed = now - upload.startTime

    upload.progress = progress
    upload.loaded = loaded
    upload.total = total

    // 计算上传速度
    if (elapsed > 0) {
      upload.speed = loaded / (elapsed / 1000) // bytes per second
      upload.timeRemaining = upload.speed > 0 ? (total - loaded) / upload.speed : 0
    }

    // 触发进度更新事件
    if (upload.onProgress) {
      upload.onProgress(upload)
    }
  }

  // 开始上传
  startUpload(id) {
    const upload = this.uploads.get(id)
    if (!upload) return

    upload.status = 'uploading'
    upload.startTime = Date.now()
  }

  // 完成上传
  completeUpload(id, result) {
    const upload = this.uploads.get(id)
    if (!upload) return

    upload.status = 'success'
    upload.endTime = Date.now()
    upload.result = result
    upload.progress = 100

    if (upload.onSuccess) {
      upload.onSuccess(result)
    }
  }

  // 上传失败
  failUpload(id, error) {
    const upload = this.uploads.get(id)
    if (!upload) return

    upload.status = 'error'
    upload.endTime = Date.now()
    upload.error = error

    if (upload.onError) {
      upload.onError(error)
    }
  }

  // 取消上传
  cancelUpload(id) {
    const upload = this.uploads.get(id)
    if (!upload) return

    upload.status = 'cancelled'
    upload.endTime = Date.now()

    if (upload.cancelToken) {
      upload.cancelToken.cancel('用户取消上传')
    }

    if (upload.onCancel) {
      upload.onCancel()
    }
  }

  // 获取上传信息
  getUpload(id) {
    return this.uploads.get(id)
  }

  // 获取所有上传
  getAllUploads() {
    return Array.from(this.uploads.values())
  }

  // 清理完成的上传
  cleanup(maxAge = 5 * 60 * 1000) {
    // 5分钟
    const now = Date.now()

    for (const [id, upload] of this.uploads.entries()) {
      if (upload.endTime && now - upload.endTime > maxAge) {
        this.uploads.delete(id)
      }
    }
  }
}

/**
 * 分片上传管理器
 */
export class ChunkedUploadManager {
  constructor(options = {}) {
    this.options = {
      chunkSize: 1024 * 1024, // 1MB
      maxRetries: 3,
      retryDelay: 1000,
      ...options
    }
  }

  // 分片上传文件
  async uploadFile(file, uploadUrl, options = {}) {
    const chunks = this.createChunks(file)
    const uploadId = this.generateUploadId()

    const results = []
    let uploadedSize = 0

    for (let i = 0; i < chunks.length; i++) {
      const chunk = chunks[i]
      let retries = 0

      while (retries < this.options.maxRetries) {
        try {
          const result = await this.uploadChunk(
            chunk,
            i,
            chunks.length,
            uploadId,
            uploadUrl,
            options
          )

          results.push(result)
          uploadedSize += chunk.size

          // 更新进度
          if (options.onProgress) {
            options.onProgress({
              loaded: uploadedSize,
              total: file.size,
              progress: (uploadedSize / file.size) * 100
            })
          }

          break
        } catch (error) {
          retries++

          if (retries >= this.options.maxRetries) {
            throw new Error(`分片 ${i} 上传失败: ${error.message}`)
          }

          // 等待后重试
          await this.delay(this.options.retryDelay * retries)
        }
      }
    }

    // 合并分片
    return this.mergeChunks(uploadId, uploadUrl, options)
  }

  // 创建文件分片
  createChunks(file) {
    const chunks = []
    const chunkSize = this.options.chunkSize

    for (let start = 0; start < file.size; start += chunkSize) {
      const end = Math.min(start + chunkSize, file.size)
      const chunk = file.slice(start, end)
      chunks.push(chunk)
    }

    return chunks
  }

  // 上传单个分片
  async uploadChunk(chunk, index, total, uploadId, uploadUrl, options) {
    const formData = new FormData()
    formData.append('chunk', chunk)
    formData.append('index', index)
    formData.append('total', total)
    formData.append('uploadId', uploadId)

    const response = await fetch(`${uploadUrl}/chunk`, {
      method: 'POST',
      body: formData,
      headers: options.headers || {}
    })

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }

    return response.json()
  }

  // 合并分片
  async mergeChunks(uploadId, uploadUrl, options) {
    const response = await fetch(`${uploadUrl}/merge`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(options.headers || {})
      },
      body: JSON.stringify({ uploadId })
    })

    if (!response.ok) {
      throw new Error(`合并失败: HTTP ${response.status}`)
    }

    return response.json()
  }

  // 生成上传ID
  generateUploadId() {
    return Date.now().toString(36) + Math.random().toString(36).substr(2)
  }

  // 延迟函数
  delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms))
  }
}

// 创建全局实例
export const fileUploadManager = new FileUploadManager()
export const uploadProgressManager = new UploadProgressManager()
export const chunkedUploadManager = new ChunkedUploadManager()

// 便捷函数
export async function uploadFile(file, url, options = {}) {
  // 验证和处理文件
  const validation = fileUploadManager.validateFile(file)
  if (!validation.valid) {
    ElMessage.error(validation.errors.join(', '))
    throw new Error(validation.errors.join(', '))
  }

  // 压缩图片
  let processedFile = file
  if (file.type.startsWith('image/') && options.compress !== false) {
    try {
      processedFile = await fileUploadManager.compressImage(file)
    } catch (error) {
      console.warn('图片压缩失败，使用原文件:', error)
    }
  }

  // 创建FormData
  const formData = new FormData()
  formData.append(options.fieldName || 'file', processedFile)

  // 添加额外字段
  if (options.data) {
    Object.entries(options.data).forEach(([key, value]) => {
      formData.append(key, value)
    })
  }

  return formData
}

export default {
  FileUploadManager,
  UploadProgressManager,
  ChunkedUploadManager,
  fileUploadManager,
  uploadProgressManager,
  chunkedUploadManager,
  uploadFile
}
