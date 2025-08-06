#!/bin/bash

echo "启动森林病虫害防治系统开发环境..."

echo ""
echo "1. 启动后端服务..."
cd backend
mvn spring-boot:run -Dmaven.test.skip=true &
BACKEND_PID=$!

echo ""
echo "2. 等待后端服务启动..."
sleep 10

echo ""
echo "3. 启动前端服务..."
cd ../frontend
npm run dev &
FRONTEND_PID=$!

echo ""
echo "开发环境启动完成！"
echo "后端服务: http://localhost:8080/api"
echo "前端服务: http://localhost:5173"
echo "API测试页面: http://localhost:5173/#/test/api"
echo ""
echo "按 Ctrl+C 停止所有服务"

# 等待用户中断
trap "echo '正在停止服务...'; kill $BACKEND_PID $FRONTEND_PID; exit" INT
wait