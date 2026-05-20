USE lab_reservation;

INSERT INTO users (id, student_no, name, username, password_hash, role, status) VALUES
  (1, 'admin', '管理员', 'admin', '123456', 'ADMIN', 1),
  (2, 'stu001', '学生一', 'stu001', '123456', 'STUDENT', 1),
  (3, 'stu002', '学生二', 'stu002', '123456', 'STUDENT', 1);

INSERT INTO equipment (id, asset_no, name, category, location, status) VALUES
  (1, 'LAB-PC-001', '高性能工作站', '计算设备', '实验楼 A301', 1),
  (2, 'LAB-OSC-001', '数字示波器', '电子仪器', '实验楼 B204', 1),
  (3, 'LAB-PRN-001', '3D 打印机', '制造设备', '创新实验室 C102', 3),
  (4, 'LAB-CAM-001', '高速摄像机', '采集设备', '实验楼 A206', 1);
