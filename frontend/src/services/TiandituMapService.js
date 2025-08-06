// 天地图服务封装类
export class TiandituMapService {
  constructor(containerId, options = {}) {
    this.containerId = containerId
    this.options = {
      center: options.center || [116.397428, 39.90923],
      zoom: options.zoom || 10,
      minZoom: options.minZoom || 3,
      maxZoom: options.maxZoom || 18,
      apiKey: options.apiKey || import.meta.env.VITE_TIANDITU_API_KEY
    }
    this.map = null
    this.layers = new Map()
    this.eventListeners = new Map()
  }

  // 初始化地图
  async initialize() {
    try {
      console.log('开始加载天地图API，密钥:', this.options.apiKey)
      await this.loadTiandituAPI()
      console.log('天地图API加载成功')
      
      console.log('开始创建地图实例')
      await this.createMap()
      console.log('地图实例创建成功')
      
      console.log('开始添加默认图层')
      this.addDefaultLayers()
      console.log('默认图层添加完成')
      
      return this.map
    } catch (error) {
      console.error('地图初始化失败:', error)
      throw error
    }
  }

  // 动态加载天地图API
  loadTiandituAPI() {
    return new Promise((resolve, reject) => {
      if (window.T) {
        console.log('天地图API已存在')
        resolve()
        return
      }

      // 检查API密钥
      if (!this.options.apiKey) {
        reject(new Error('天地图API密钥未配置'))
        return
      }

      console.log('开始加载天地图API脚本')
      const script = document.createElement('script')
      script.src = `https://api.tianditu.gov.cn/api?v=4.0&tk=${this.options.apiKey}`
      
      // 设置超时
      const timeout = setTimeout(() => {
        console.error('天地图API加载超时')
        reject(new Error('天地图API加载超时'))
      }, 15000) // 15秒超时
      
      script.onload = () => {
        clearTimeout(timeout)
        console.log('天地图API脚本加载完成')
        
        // 等待一下确保API完全初始化
        setTimeout(() => {
          if (window.T) {
            console.log('天地图API初始化完成')
            resolve()
          } else {
            reject(new Error('天地图API初始化失败'))
          }
        }, 1000)
      }
      
      script.onerror = () => {
        clearTimeout(timeout)
        console.error('天地图API脚本加载失败')
        reject(new Error('天地图API加载失败，请检查网络连接和API密钥'))
      }
      
      document.head.appendChild(script)
    })
  }

  // 创建地图实例
  createMap() {
    return new Promise((resolve, reject) => {
      try {
        // 检查容器是否存在
        const container = document.getElementById(this.containerId)
        if (!container) {
          reject(new Error(`地图容器 ${this.containerId} 不存在`))
          return
        }

        console.log('创建地图实例，容器ID:', this.containerId)
        console.log('地图配置:', this.options)

        this.map = new window.T.Map(this.containerId, {
          center: new window.T.LngLat(this.options.center[0], this.options.center[1]),
          zoom: this.options.zoom,
          minZoom: this.options.minZoom,
          maxZoom: this.options.maxZoom
        })
        
        console.log('地图实例创建完成:', this.map)
        
        // 直接resolve，不等待load事件
        setTimeout(() => {
          resolve(this.map)
        }, 1000)
        
      } catch (error) {
        console.error('创建地图实例失败:', error)
        reject(error)
      }
    })
  }

  // 添加默认图层
  addDefaultLayers() {
    try {
      console.log('开始添加默认图层')
      // 添加卫星底图
      this.addBaseLayer('satellite')
      
      // 添加标注图层
      this.addAnnotationLayer()
      console.log('默认图层添加完成')
    } catch (error) {
      console.error('添加默认图层失败:', error)
      // 即使图层添加失败，也不抛出错误，让地图能够显示
    }
  }

  // 添加底图图层
  addBaseLayer(layerType) {
    try {
      console.log(`开始添加${layerType}图层`)
      
      // 使用更简单的瓦片URL格式
      let tileUrl = ''
      switch (layerType) {
        case 'satellite':
          tileUrl = `https://t{s}.tianditu.gov.cn/img_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=img&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=${this.options.apiKey}`
          break
        case 'vector':
          tileUrl = `https://t{s}.tianditu.gov.cn/vec_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=${this.options.apiKey}`
          break
        case 'terrain':
          tileUrl = `https://t{s}.tianditu.gov.cn/ter_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=ter&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=${this.options.apiKey}`
          break
        default:
          return
      }

      // 检查T.TileLayer是否存在
      if (!window.T || !window.T.TileLayer) {
        console.error('天地图API未正确加载')
        return
      }

      const layer = new window.T.TileLayer(tileUrl, {
        subdomains: ['0', '1', '2', '3', '4', '5', '6', '7']
      })

      this.map.addLayer(layer)
      this.layers.set(`base_${layerType}`, layer)
      console.log(`${layerType}图层添加成功`)
    } catch (error) {
      console.error(`添加${layerType}图层失败:`, error)
    }
  }

  // 添加标注图层
  addAnnotationLayer() {
    try {
      const annotationUrl = `https://t{s}.tianditu.gov.cn/cva_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cva&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&tk=${this.options.apiKey}`
      
      const layer = new window.T.TileLayer(annotationUrl, {
        subdomains: ['0', '1', '2', '3', '4', '5', '6', '7']
      })

      this.map.addLayer(layer)
      this.layers.set('annotation', layer)
      console.log('标注图层添加成功')
    } catch (error) {
      console.error('添加标注图层失败:', error)
    }
  }

  // 添加标记点
  addMarker(position, options = {}) {
    console.log('TiandituMapService.addMarker 调用')
    console.log('位置:', position)
    console.log('选项:', options)
    console.log('window.T 存在:', !!window.T)
    console.log('地图实例存在:', !!this.map)
    
    // 检查天地图API是否可用
    if (!window.T) {
      console.error('天地图API (window.T) 不存在')
      return null
    }
    
    if (!this.map) {
      console.error('地图实例不存在')
      return null
    }

    try {
      console.log('开始创建标记点')
      
      const lngLat = new window.T.LngLat(position[0], position[1])
      console.log('经纬度对象创建成功:', lngLat)
      
      const icon = new window.T.Icon({
        iconUrl: options.iconUrl || '/icons/forest/default.svg',
        iconSize: new window.T.Point(options.size?.[0] || 32, options.size?.[1] || 32),
        iconAnchor: new window.T.Point(options.anchor?.[0] || 16, options.anchor?.[1] || 32)
      })
      console.log('图标对象创建成功:', icon)
      
      const marker = new window.T.Marker(lngLat, { icon: icon })
      console.log('标记对象创建成功:', marker)

      // 添加点击事件
      if (options.onClick) {
        marker.addEventListener('click', (e) => {
          console.log('标记点击事件触发')
          options.onClick(e, options.data)
        })
      }

      console.log('准备添加标记到地图')
      this.map.addOverLay(marker)
      console.log('标记点添加到地图成功:', position)
      
      return marker
    } catch (error) {
      console.error('添加标记点时发生错误:', error)
      console.error('错误堆栈:', error.stack)
      return null
    }
  }

  // 添加多边形
  addPolygon(coordinates, options = {}) {
    const points = coordinates.map(coord => new window.T.LngLat(coord[0], coord[1]))
    const polygon = new window.T.Polygon(points, {
      color: options.strokeColor || '#ff0000',
      weight: options.strokeWeight || 2,
      opacity: options.strokeOpacity || 0.8,
      fillColor: options.fillColor || '#ff0000',
      fillOpacity: options.fillOpacity || 0.3
    })

    this.map.addOverLay(polygon)
    return polygon
  }

  // 事件监听
  on(eventType, callback) {
    if (!this.eventListeners.has(eventType)) {
      this.eventListeners.set(eventType, [])
    }
    this.eventListeners.get(eventType).push(callback)

    if (this.map) {
      this.map.addEventListener(eventType, callback)
    }
  }

  // 获取地图实例
  getMap() {
    return this.map
  }

  // 获取地图边界
  getBounds() {
    if (!this.map) return null
    return this.map.getBounds()
  }

  // 获取缩放级别
  getZoom() {
    if (!this.map) return this.options.zoom
    return this.map.getZoom()
  }

  // 开始测量
  startMeasure(mode) {
    // 测量功能的简化实现
    console.log('开始测量:', mode)
  }

  // 获取指定像素位置的要素
  getFeatureAtPixel(pixel) {
    // 简化实现，实际需要根据天地图API实现
    return null
  }

  // 销毁地图
  destroy() {
    if (this.map) {
      this.map.clearOverLays()
      this.map = null
    }
    this.layers.clear()
    this.eventListeners.clear()
  }
}