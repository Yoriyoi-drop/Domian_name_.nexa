// src/core/api/common/response-wrapper.ts
import { ApiResponse } from './api-types';

export class ResponseWrapper<T> {
  private readonly response: ApiResponse<T>;

  constructor(response: ApiResponse<T>) {
    this.response = response;
  }

  isSuccess(): boolean {
    return this.response.success === true;
  }

  isError(): boolean {
    return !this.isSuccess();
  }

  getData(): T | undefined {
    return this.response.data;
  }

  getMessage(): string | undefined {
    return this.response.message;
  }

  getErrors(): string[] | undefined {
    return this.response.errors;
  }

  getErrorCode(): string | undefined {
    return this.response.errorCode;
  }

  unwrap(): T {
    if (this.isError()) {
      throw new Error(this.getMessage() || 'Unknown error occurred');
    }
    return this.getData()!;
  }

  static from<T>(data: T): ApiResponse<T> {
    return {
      success: true,
      data,
      timestamp: new Date().toISOString()
    };
  }

  static error<T>(message: string, errors?: string[]): ApiResponse<T> {
    return {
      success: false,
      message,
      errors,
      timestamp: new Date().toISOString()
    };
  }
}