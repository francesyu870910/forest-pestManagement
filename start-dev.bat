@echo off
echo 启动森林病虫害防治系统开发环境...

echo.
echo 1. 启动后端服务...
start "Backend Server" cmd /k "cd backend && mvn spring-boot:run"

echo.
echo 2. 等待后端服务启动...
timeout /t 10 /nobreak > nul

echo.
echo 3. 启动前端服务...
start "Frontend Server" cmd /k "cd frontend && npm run dev"

echo.
echo 开发环境启动完成！
echo 后端服务: http://localhost:8080/api
echo 前端服务: http://localhost:5173
echo API测试页面: http://localhost:5173/#/test/api
echo.
pause