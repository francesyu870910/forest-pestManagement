@echo off
echo 正在修复Java导入问题...

echo 修复 javax.validation 导入...
powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/controller/PredictionController.java') -replace 'import javax.validation', 'import jakarta.validation' | Set-Content 'backend/src/main/java/com/forestpest/controller/PredictionController.java'"
powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/controller/PredictionController.java') -replace 'import javax.validation.constraints', 'import jakarta.validation.constraints' | Set-Content 'backend/src/main/java/com/forestpest/controller/PredictionController.java'"

powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/controller/PesticideController.java') -replace 'import javax.validation', 'import jakarta.validation' | Set-Content 'backend/src/main/java/com/forestpest/controller/PesticideController.java'"
powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/controller/PesticideController.java') -replace 'import javax.validation.constraints', 'import jakarta.validation.constraints' | Set-Content 'backend/src/main/java/com/forestpest/controller/PesticideController.java'"

powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/controller/PestIdentificationController.java') -replace 'import javax.validation', 'import jakarta.validation' | Set-Content 'backend/src/main/java/com/forestpest/controller/PestIdentificationController.java'"
powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/controller/PestIdentificationController.java') -replace 'import javax.validation.constraints', 'import jakarta.validation.constraints' | Set-Content 'backend/src/main/java/com/forestpest/controller/PestIdentificationController.java'"

powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/controller/EffectEvaluationController.java') -replace 'import javax.validation', 'import jakarta.validation' | Set-Content 'backend/src/main/java/com/forestpest/controller/EffectEvaluationController.java'"
powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/controller/EffectEvaluationController.java') -replace 'import javax.validation.constraints', 'import jakarta.validation.constraints' | Set-Content 'backend/src/main/java/com/forestpest/controller/EffectEvaluationController.java'"

powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/controller/TreatmentPlanController.java') -replace 'import javax.validation', 'import jakarta.validation' | Set-Content 'backend/src/main/java/com/forestpest/controller/TreatmentPlanController.java'"
powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/controller/TreatmentPlanController.java') -replace 'import javax.validation.constraints', 'import jakarta.validation.constraints' | Set-Content 'backend/src/main/java/com/forestpest/controller/TreatmentPlanController.java'"

powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/controller/UserController.java') -replace 'import javax.validation', 'import jakarta.validation' | Set-Content 'backend/src/main/java/com/forestpest/controller/UserController.java'"

echo 修复 javax.servlet 导入...
powershell -Command "(Get-Content 'backend/src/main/java/com/forestpest/interceptor/AuthInterceptor.java') -replace 'import javax.servlet.http', 'import jakarta.servlet.http' | Set-Content 'backend/src/main/java/com/forestpest/interceptor/AuthInterceptor.java'"

echo 导入修复完成！
pause