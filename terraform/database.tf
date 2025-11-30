# Database Parameter Group
resource "aws_db_parameter_group" "postgres" {
  name   = "myproject-nexa-${var.environment}-postgres-pg"
  family = "postgres15"

  parameter {
    name  = "max_connections"
    value = "100"
  }

  tags = {
    Name = "myproject-nexa-${var.environment}-postgres-pg"
  }
}

# RDS Instance
resource "aws_db_instance" "main" {
  identifier             = "myproject-nexa-${var.environment}-db"
  allocated_storage      = 20
  storage_type           = "gp2"
  engine                 = "postgres"
  engine_version         = "15.4"
  instance_class         = var.database_instance_class
  db_name                = var.database_name
  username               = var.database_username
  password               = var.database_password
  parameter_group_name   = aws_db_parameter_group.postgres.name
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.database.id]
  skip_final_snapshot    = true
  publicly_accessible    = false
  multi_az               = var.environment == "prod" ? true : false

  tags = {
    Name        = "myproject-nexa-${var.environment}-db"
    Environment = var.environment
  }
}

# Database Secrets in Secrets Manager
resource "aws_secretsmanager_secret" "database" {
  name = "myproject-nexa/${var.environment}/database-credentials"
}

resource "aws_secretsmanager_secret_version" "database" {
  secret_id     = aws_secretsmanager_secret.database.id
  secret_string = jsonencode({
    username = var.database_username
    password = var.database_password
    host     = aws_db_instance.main.address
    port     = aws_db_instance.main.port
    dbname   = var.database_name
    jdbc_url = "jdbc:postgresql://${aws_db_instance.main.address}:${aws_db_instance.main.port}/${var.database_name}"
  })
}