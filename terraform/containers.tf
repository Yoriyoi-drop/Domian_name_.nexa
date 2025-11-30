# ECR Repositories
resource "aws_ecr_repository" "backend" {
  name                 = "myproject-nexa-${var.environment}-backend"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "myproject-nexa-${var.environment}-backend"
  }
}

resource "aws_ecr_repository" "frontend" {
  name                 = "myproject-nexa-${var.environment}-frontend"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "myproject-nexa-${var.environment}-frontend"
  }
}

# Application Load Balancer
resource "aws_lb" "app" {
  name               = "myproject-nexa-${var.environment}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = aws_subnet.public[*].id

  tags = {
    Name        = "myproject-nexa-${var.environment}-alb"
    Environment = var.environment
  }
}

# ALB Security Group
resource "aws_security_group" "alb" {
  name_prefix = "myproject-nexa-${var.environment}-alb-"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = var.application_port
    to_port     = var.application_port
    protocol    = "tcp"
    cidr_blocks = [for s in aws_subnet.private : s.cidr_block]
  }

  tags = {
    Name = "myproject-nexa-${var.environment}-alb-sg"
  }
}

# ALB Listener for HTTP (redirects to HTTPS)
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.app.id
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "redirect"

    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

# ALB Listener for HTTPS
resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_lb.app.id
  port              = "443"
  protocol          = "HTTPS"
  certificate_arn   = var.ssl_certificate_arn != "" ? var.ssl_certificate_arn : null

  # If no SSL certificate is provided, use HTTP for local development
  count = var.ssl_certificate_arn != "" ? 1 : 0

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend.arn
  }
}

# Target Group for Backend
resource "aws_lb_target_group" "backend" {
  name     = "myproject-nexa-${var.environment}-backend-tg"
  port     = var.application_port
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id

  health_check {
    path                = "/actuator/health"
    protocol            = "HTTP"
    matcher             = "200"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 3
  }

  tags = {
    Name = "myproject-nexa-${var.environment}-backend-tg"
  }
}

# Target Group for Frontend (if needed)
resource "aws_lb_target_group" "frontend" {
  name     = "myproject-nexa-${var.environment}-frontend-tg"
  port     = var.frontend_port
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id

  health_check {
    path    = "/"
    matcher = "200"
  }

  tags = {
    Name = "myproject-nexa-${var.environment}-frontend-tg"
  }
}