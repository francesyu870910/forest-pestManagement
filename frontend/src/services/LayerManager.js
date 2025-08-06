// 图层管理器
export class LayerManager {
  constructor(mapService) {
    this.mapService = mapService
    this.layers = new Map()
    this.clusteringEnabled = false
  }

  // 添加森林资源图层
  async addForestLayer(data) {
    console.log('开始添加森林资源图层，数据:', data)
    const markers = []
    
    for (const resource of data) {
      if (!resource.longitude || !resource.latitude) {
        console.warn('资源缺少坐标信息:', resource)
        continue
      }
      
      console.log('添加森林标记:', resource.name, resource.longitude, resource.latitude)
      
      try {
        const marker = this.mapService.addMarker(
          [resource.longitude, resource.latitude],
          {
            iconUrl: this.getForestIcon(resource.type),
            size: [32, 32],
            anchor: [16, 32],
            data: { ...resource, type: 'forest' },
            onClick: (e, data) => this.handleMarkerClick(data, e)
          }
        )
        
        if (marker) {
          markers.push(marker)
          console.log('森林标记添加成功:', resource.name)
        } else {
          console.error('森林标记添加失败:', resource.name)
        }
      } catch (error) {
        console.error('添加森林标记时出错:', error, resource)
      }
    }
    
    console.log(`森林图层添加完成，共${markers.length}个标记`)
    
    this.layers.set('forest', {
      type: 'markers',
      data: markers,
      visible: true,
      opacity: 1
    })
  }

  // 添加病虫害图层
  async addPestLayer(data) {
    const markers = []
    
    for (const pest of data) {
      if (!pest.longitude || !pest.latitude) continue
      
      const marker = this.mapService.addMarker(
        [pest.longitude, pest.latitude],
        {
          iconUrl: this.getPestIcon(pest.type, pest.severity),
          size: [24, 24],
          anchor: [12, 24],
          data: { ...pest, type: 'pest' },
          onClick: (e, data) => this.handleMarkerClick(data, e)
        }
      )
      
      markers.push(marker)
    }
    
    this.layers.set('pest', {
      type: 'markers',
      data: markers,
      visible: true,
      opacity: 1
    })
  }

  // 添加防治区域图层
  async addTreatmentLayer(data) {
    const polygons = []
    
    for (const area of data) {
      if (!area.coordinates || !Array.isArray(area.coordinates)) continue
      
      const polygon = this.mapService.addPolygon(area.coordinates, {
        strokeColor: this.getTreatmentColor(area.status),
        strokeWeight: 2,
        strokeOpacity: 0.8,
        fillColor: this.getTreatmentColor(area.status),
        fillOpacity: 0.3
      })
      
      polygons.push(polygon)
    }
    
    this.layers.set('treatment', {
      type: 'polygons',
      data: polygons,
      visible: true,
      opacity: 1
    })
  }

  // 切换图层显示/隐藏
  toggleLayer(layerName, visible) {
    const layer = this.layers.get(layerName)
    if (!layer) return

    layer.visible = visible
    
    // 实现显示/隐藏逻辑
    layer.data.forEach(item => {
      if (item.setVisible) {
        item.setVisible(visible)
      } else if (item.show && item.hide) {
        visible ? item.show() : item.hide()
      }
    })
  }

  // 设置图层透明度
  setLayerOpacity(layerName, opacity) {
    const layer = this.layers.get(layerName)
    if (!layer) return

    layer.opacity = opacity
    
    // 实现透明度设置逻辑
    layer.data.forEach(item => {
      if (item.setOpacity) {
        item.setOpacity(opacity)
      }
    })
  }

  // 启用/禁用聚合
  enableClustering(enabled) {
    this.clusteringEnabled = enabled
    // 实现聚合逻辑
  }

  // 更新森林资源图层
  updateForestLayer(data) {
    this.clearLayer('forest')
    this.addForestLayer(data)
  }

  // 更新病虫害图层
  updatePestLayer(data) {
    this.clearLayer('pest')
    this.addPestLayer(data)
  }

  // 更新防治区域图层
  updateTreatmentLayer(data) {
    this.clearLayer('treatment')
    this.addTreatmentLayer(data)
  }

  // 清除指定图层
  clearLayer(layerName) {
    const layer = this.layers.get(layerName)
    if (!layer) return

    // 从地图上移除所有要素
    layer.data.forEach(item => {
      if (this.mapService.map && this.mapService.map.removeOverLay) {
        this.mapService.map.removeOverLay(item)
      }
    })

    this.layers.delete(layerName)
  }

  // 导出数据
  exportData() {
    const exportData = {}
    
    this.layers.forEach((layer, name) => {
      exportData[name] = {
        type: layer.type,
        visible: layer.visible,
        opacity: layer.opacity,
        count: layer.data.length
      }
    })
    
    return exportData
  }

  // 获取森林类型图标
  getForestIcon(type) {
    const iconMap = {
      natural_forest: '/icons/forest/natural.svg',
      artificial_forest: '/icons/forest/artificial.svg',
      economic_forest: '/icons/forest/economic.svg',
      protection_forest: '/icons/forest/protection.svg'
    }
    const iconUrl = iconMap[type] || '/icons/forest/default.svg'
    console.log(`获取森林图标: ${type} -> ${iconUrl}`)
    return iconUrl
  }

  // 获取病虫害图标
  getPestIcon(type, severity) {
    const severityColors = {
      low: 'green',
      medium: 'orange',
      high: 'red',
      critical: 'darkred'
    }
    const color = severityColors[severity] || 'gray'
    return `/icons/pest/pest-${color}.svg`
  }

  // 获取防治区域颜色
  getTreatmentColor(status) {
    const colorMap = {
      planned: '#409EFF',
      active: '#E6A23C',
      completed: '#67C23A',
      paused: '#F56C6C'
    }
    return colorMap[status] || '#909399'
  }

  // 处理标记点击事件
  handleMarkerClick(data, event) {
    // 触发标记点击事件，由地图组件处理
    console.log('标记点击:', data, event)
  }
}