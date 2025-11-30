terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  required_version = "~> 1.0"

  # Backend configuration for remote state management
  # This should be configured based on your environment
  # backend "s3" {
  #   bucket         = "myproject-nexa-terraform-state"
  #   key            = "terraform.tfstate"
  #   region         = "us-east-1"
  #   encrypt        = true
  #   dynamodb_table = "terraform-locks"
  # }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "MyProject.nexa"
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}