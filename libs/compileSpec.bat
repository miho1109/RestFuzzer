@echo off
echo Batch Script compile spec file.
set /p input= Spec file path:
".\restler-bin\restler\Restler.exe" compile --api_spec %input%
ping 127.0.0.1 -n 6 > nul
exit 0