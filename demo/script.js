// 简化版防治方案管理系统 - JavaScript逻辑

// 数据存储 (模拟简化版TreatmentPlanServiceImpl的功能)
let treatmentPlans = [];
let treatmentTasks = [];
let currentUser = 'demo-user';

// 生成唯一ID
function generateId() {
    return 'id_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
}

// 获取当前时间字符串
function getCurrentTime() {
    return new Date().toLocaleString('zh-CN');
}

// 标签切换功能
function showTab(tabName) {
    // 隐藏所有标签内容
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    
    // 移除所有标签按钮的激活状态
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // 显示选中的标签内容
    document.getElementById(tabName).classList.add('active');
    
    // 激活对应的标签按钮
    event.target.classList.add('active');
    
    // 如果切换到进度统计，更新统计数据
    if (tabName === 'progress') {
        updateProgressStats();
    }
}

// 显示消息提示
function showMessage(text, type = 'success') {
    const messageEl = document.getElementById('message');
    messageEl.textContent = text;
    messageEl.className = `message ${type}`;
    messageEl.classList.add('show');
    
    setTimeout(() => {
        messageEl.classList.remove('show');
    }, 3000);
}

// 模态框控制
function showCreatePlanModal() {
    document.getElementById('createPlanModal').style.display = 'block';
}

function showCreateTaskModal() {
    // 更新任务创建表单中的方案选项
    updateTaskPlanOptions();
    document.getElementById('createTaskModal').style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
    // 清空表单
    if (modalId === 'createPlanModal') {
        document.getElementById('createPlanForm').reset();
    } else if (modalId === 'createTaskModal') {
        document.getElementById('createTaskForm').reset();
    }
}

// 点击模态框外部关闭
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
    }
}

// 创建防治方案 (模拟generateTreatmentPlan方法)
function createPlan() {
    const pestType = document.getElementById('pestType').value;
    const planName = document.getElementById('planName').value;
    const description = document.getElementById('planDescription').value;
    const targetArea = document.getElementById('targetArea').value;
    const estimatedCost = document.getElementById('estimatedCost').value;
    const estimatedDuration = document.getElementById('estimatedDuration').value;
    
    if (!pestType || !planName) {
        showMessage('请填写必要信息', 'error');
        return;
    }
    
    const plan = {
        id: generateId(),
        pestId: pestType,
        planName: planName,
        description: description || `针对${pestType}的防治方案`,
        targetArea: targetArea,
        estimatedCost: estimatedCost ? parseFloat(estimatedCost) : null,
        estimatedDuration: estimatedDuration ? parseInt(estimatedDuration) : null,
        createdBy: currentUser,
        createdTime: getCurrentTime(),
        status: 'DRAFT',
        priority: 'MEDIUM',
        methods: [{
            methodType: '综合防治',
            methodName: '标准防治方案',
            description: `采用综合防治方法控制${pestType}`
        }]
    };
    
    treatmentPlans.push(plan);
    closeModal('createPlanModal');
    renderPlans();
    showMessage('防治方案创建成功！');
}

// 渲染防治方案列表 (模拟getUserTreatmentPlans方法)
function renderPlans() {
    const plansGrid = document.getElementById('plansGrid');
    
    if (treatmentPlans.length === 0) {
        plansGrid.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #666;">
                <h3>暂无防治方案</h3>
                <p>点击"创建方案"按钮开始创建您的第一个防治方案</p>
            </div>
        `;
        return;
    }
    
    plansGrid.innerHTML = treatmentPlans.map(plan => `
        <div class="plan-card">
            <h3>${plan.planName}</h3>
            <div class="pest-type">${plan.pestId}</div>
            <div class="description">${plan.description}</div>
            <div class="meta">
                <span>创建时间: ${plan.createdTime}</span>
                <span>状态: ${getStatusText(plan.status)}</span>
            </div>
            <div class="meta">
                ${plan.targetArea ? `<span>目标区域: ${plan.targetArea}</span>` : ''}
                ${plan.estimatedCost ? `<span>预估成本: ¥${plan.estimatedCost}</span>` : ''}
            </div>
            <div class="actions">
                <button class="btn btn-sm btn-primary" onclick="viewPlan('${plan.id}')">查看详情</button>
                <button class="btn btn-sm btn-warning" onclick="editPlan('${plan.id}')">编辑</button>
                <button class="btn btn-sm btn-danger" onclick="deletePlan('${plan.id}')">删除</button>
            </div>
        </div>
    `).join('');
}

// 获取状态文本
function getStatusText(status) {
    const statusMap = {
        'DRAFT': '草稿',
        'ACTIVE': '执行中',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
    };
    return statusMap[status] || status;
}

// 获取任务状态文本
function getTaskStatusText(status) {
    const statusMap = {
        'PENDING': '待执行',
        'ASSIGNED': '已分配',
        'IN_PROGRESS': '执行中',
        'COMPLETED': '已完成'
    };
    return statusMap[status] || status;
}

// 查看方案详情
function viewPlan(planId) {
    const plan = treatmentPlans.find(p => p.id === planId);
    if (plan) {
        alert(`方案详情：\n\n名称：${plan.planName}\n病虫害：${plan.pestId}\n描述：${plan.description}\n创建时间：${plan.createdTime}\n状态：${getStatusText(plan.status)}`);
    }
}

// 编辑方案 (简化实现)
function editPlan(planId) {
    const plan = treatmentPlans.find(p => p.id === planId);
    if (plan) {
        const newName = prompt('请输入新的方案名称：', plan.planName);
        if (newName && newName !== plan.planName) {
            plan.planName = newName;
            plan.updatedTime = getCurrentTime();
            plan.updatedBy = currentUser;
            renderPlans();
            showMessage('方案更新成功！');
        }
    }
}

// 删除方案 (模拟deleteTreatmentPlan方法)
function deletePlan(planId) {
    if (confirm('确定要删除这个防治方案吗？相关任务也会被删除。')) {
        // 删除相关任务
        treatmentTasks = treatmentTasks.filter(task => task.planId !== planId);
        
        // 删除方案
        treatmentPlans = treatmentPlans.filter(plan => plan.id !== planId);
        
        renderPlans();
        renderTasks();
        showMessage('方案删除成功！');
    }
}

// 更新任务创建表单中的方案选项
function updateTaskPlanOptions() {
    const select = document.getElementById('taskPlanId');
    select.innerHTML = '<option value="">请选择关联方案</option>';
    
    treatmentPlans.forEach(plan => {
        select.innerHTML += `<option value="${plan.id}">${plan.planName}</option>`;
    });
}

// 创建防治任务 (模拟createTreatmentTask方法)
function createTask() {
    const planId = document.getElementById('taskPlanId').value;
    const taskName = document.getElementById('taskName').value;
    const description = document.getElementById('taskDescription').value;
    const assignedTo = document.getElementById('assignedTo').value;
    const scheduledTime = document.getElementById('scheduledTime').value;
    
    if (!planId || !taskName) {
        showMessage('请填写必要信息', 'error');
        return;
    }
    
    const task = {
        id: generateId(),
        planId: planId,
        taskName: taskName,
        description: description,
        assignedTo: assignedTo || '未分配',
        status: assignedTo ? 'ASSIGNED' : 'PENDING',
        scheduledTime: scheduledTime ? new Date(scheduledTime).toLocaleString('zh-CN') : null,
        createdBy: currentUser,
        createdTime: getCurrentTime()
    };
    
    treatmentTasks.push(task);
    closeModal('createTaskModal');
    renderTasks();
    showMessage('防治任务创建成功！');
}

// 渲染防治任务列表 (模拟getTreatmentTasks方法)
function renderTasks() {
    const tasksList = document.getElementById('tasksList');
    
    if (treatmentTasks.length === 0) {
        tasksList.innerHTML = `
            <div style="text-align: center; padding: 40px; color: #666;">
                <h3>暂无防治任务</h3>
                <p>点击"创建任务"按钮开始创建您的第一个防治任务</p>
            </div>
        `;
        return;
    }
    
    tasksList.innerHTML = treatmentTasks.map(task => {
        const plan = treatmentPlans.find(p => p.id === task.planId);
        const planName = plan ? plan.planName : '未知方案';
        
        return `
            <div class="task-item ${task.status.toLowerCase()}">
                <div class="task-header">
                    <h4>${task.taskName}</h4>
                    <span class="task-status ${task.status.toLowerCase()}">${getTaskStatusText(task.status)}</span>
                </div>
                <div class="task-meta">
                    <span>关联方案: ${planName}</span>
                    <span>分配给: ${task.assignedTo}</span>
                    <span>创建时间: ${task.createdTime}</span>
                </div>
                ${task.description ? `<div style="margin: 10px 0; color: #666;">${task.description}</div>` : ''}
                ${task.scheduledTime ? `<div style="margin: 10px 0; color: #666;">计划执行: ${task.scheduledTime}</div>` : ''}
                <div class="task-actions">
                    ${task.status === 'PENDING' ? `<button class="btn btn-sm btn-primary" onclick="assignTask('${task.id}')">分配任务</button>` : ''}
                    ${task.status === 'ASSIGNED' ? `<button class="btn btn-sm btn-success" onclick="startTask('${task.id}')">开始执行</button>` : ''}
                    ${task.status === 'IN_PROGRESS' ? `<button class="btn btn-sm btn-success" onclick="completeTask('${task.id}')">完成任务</button>` : ''}
                    <button class="btn btn-sm btn-warning" onclick="editTask('${task.id}')">编辑</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteTask('${task.id}')">删除</button>
                </div>
            </div>
        `;
    }).join('');
}

// 分配任务 (模拟assignTreatmentTask方法)
function assignTask(taskId) {
    const assignee = prompt('请输入执行人员：');
    if (assignee) {
        const task = treatmentTasks.find(t => t.id === taskId);
        if (task) {
            task.assignedTo = assignee;
            task.status = 'ASSIGNED';
            task.updatedTime = getCurrentTime();
            task.updatedBy = currentUser;
            renderTasks();
            showMessage('任务分配成功！');
        }
    }
}

// 开始执行任务
function startTask(taskId) {
    const task = treatmentTasks.find(t => t.id === taskId);
    if (task) {
        task.status = 'IN_PROGRESS';
        task.actualStartTime = getCurrentTime();
        task.updatedTime = getCurrentTime();
        task.updatedBy = currentUser;
        renderTasks();
        showMessage('任务已开始执行！');
    }
}

// 完成任务 (模拟completeTreatmentTask方法)
function completeTask(taskId) {
    const notes = prompt('请输入执行备注（可选）：');
    const task = treatmentTasks.find(t => t.id === taskId);
    if (task) {
        task.status = 'COMPLETED';
        task.actualEndTime = getCurrentTime();
        task.executionNotes = notes || '';
        task.updatedTime = getCurrentTime();
        task.updatedBy = currentUser;
        renderTasks();
        showMessage('任务完成！');
    }
}

// 编辑任务
function editTask(taskId) {
    const task = treatmentTasks.find(t => t.id === taskId);
    if (task) {
        const newName = prompt('请输入新的任务名称：', task.taskName);
        if (newName && newName !== task.taskName) {
            task.taskName = newName;
            task.updatedTime = getCurrentTime();
            task.updatedBy = currentUser;
            renderTasks();
            showMessage('任务更新成功！');
        }
    }
}

// 删除任务
function deleteTask(taskId) {
    if (confirm('确定要删除这个任务吗？')) {
        treatmentTasks = treatmentTasks.filter(task => task.id !== taskId);
        renderTasks();
        showMessage('任务删除成功！');
    }
}

// 更新进度统计 (模拟getTreatmentProgress等方法)
function updateProgressStats() {
    const totalPlans = treatmentPlans.length;
    const totalTasks = treatmentTasks.length;
    const completedTasks = treatmentTasks.filter(task => task.status === 'COMPLETED').length;
    const completionRate = totalTasks > 0 ? Math.round((completedTasks / totalTasks) * 100) : 0;
    
    document.getElementById('totalPlans').textContent = totalPlans;
    document.getElementById('totalTasks').textContent = totalTasks;
    document.getElementById('completedTasks').textContent = completedTasks;
    document.getElementById('completionRate').textContent = completionRate + '%';
    
    // 更新状态分布图表
    updateStatusChart();
}

// 更新状态分布图表
function updateStatusChart() {
    const statusCounts = {
        'PENDING': 0,
        'ASSIGNED': 0,
        'IN_PROGRESS': 0,
        'COMPLETED': 0
    };
    
    treatmentTasks.forEach(task => {
        statusCounts[task.status] = (statusCounts[task.status] || 0) + 1;
    });
    
    const total = treatmentTasks.length;
    const chartContainer = document.getElementById('statusChart');
    
    if (total === 0) {
        chartContainer.innerHTML = '<p style="text-align: center; color: #666;">暂无任务数据</p>';
        return;
    }
    
    chartContainer.innerHTML = Object.entries(statusCounts).map(([status, count]) => {
        const percentage = Math.round((count / total) * 100);
        const statusText = getTaskStatusText(status);
        const statusClass = status.toLowerCase();
        
        return `
            <div class="progress-bar">
                <div class="progress-bar-label">${statusText}</div>
                <div class="progress-bar-track">
                    <div class="progress-bar-fill ${statusClass}" style="width: ${percentage}%"></div>
                </div>
                <div class="progress-bar-value">${count}</div>
            </div>
        `;
    }).join('');
}

// 初始化演示数据
function initDemoData() {
    // 创建一些演示方案
    const demoPlan1 = {
        id: generateId(),
        pestId: '松毛虫',
        planName: '松毛虫综合防治方案',
        description: '针对松毛虫的综合防治，包括生物防治和化学防治相结合',
        targetArea: '东山林区A区',
        estimatedCost: 15000,
        estimatedDuration: 30,
        createdBy: currentUser,
        createdTime: getCurrentTime(),
        status: 'DRAFT',
        priority: 'HIGH',
        methods: [{
            methodType: '综合防治',
            methodName: '生物+化学防治',
            description: '采用天敌昆虫配合低毒农药进行防治'
        }]
    };
    
    const demoPlan2 = {
        id: generateId(),
        pestId: '天牛',
        planName: '天牛物理防治方案',
        description: '采用物理防治方法控制天牛危害',
        targetArea: '西山林区B区',
        estimatedCost: 8000,
        estimatedDuration: 15,
        createdBy: currentUser,
        createdTime: getCurrentTime(),
        status: 'ACTIVE',
        priority: 'MEDIUM',
        methods: [{
            methodType: '物理防治',
            methodName: '诱捕器防治',
            description: '设置诱捕器捕获成虫'
        }]
    };
    
    treatmentPlans.push(demoPlan1, demoPlan2);
    
    // 创建一些演示任务
    const demoTask1 = {
        id: generateId(),
        planId: demoPlan1.id,
        taskName: '设置天敌昆虫释放点',
        description: '在指定区域设置天敌昆虫释放点，准备生物防治',
        assignedTo: '张三',
        status: 'ASSIGNED',
        scheduledTime: new Date(Date.now() + 24 * 60 * 60 * 1000).toLocaleString('zh-CN'),
        createdBy: currentUser,
        createdTime: getCurrentTime()
    };
    
    const demoTask2 = {
        id: generateId(),
        planId: demoPlan1.id,
        taskName: '喷洒低毒农药',
        description: '在天敌昆虫释放后一周进行低毒农药喷洒',
        assignedTo: '李四',
        status: 'PENDING',
        createdBy: currentUser,
        createdTime: getCurrentTime()
    };
    
    const demoTask3 = {
        id: generateId(),
        planId: demoPlan2.id,
        taskName: '安装诱捕器',
        description: '在西山林区B区安装天牛诱捕器',
        assignedTo: '王五',
        status: 'COMPLETED',
        actualEndTime: getCurrentTime(),
        executionNotes: '已完成20个诱捕器的安装',
        createdBy: currentUser,
        createdTime: getCurrentTime()
    };
    
    treatmentTasks.push(demoTask1, demoTask2, demoTask3);
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 初始化演示数据
    initDemoData();
    
    // 渲染初始界面
    renderPlans();
    renderTasks();
    updateProgressStats();
    
    // 显示欢迎消息
    setTimeout(() => {
        showMessage('欢迎使用森林病虫害防治方案管理系统演示版！', 'info');
    }, 500);
});