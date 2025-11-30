// src/core/hooks/useApi.ts
import { useState, useCallback } from 'react';
import { ResponseWrapper } from '../api/common/response-wrapper';

type ApiCallFn<T> = () => Promise<any>;

interface UseApiReturn<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
  execute: (apiCall: ApiCallFn<T>) => Promise<void>;
}

export const useApi = <T>(): UseApiReturn<T> => {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const execute = useCallback(async (apiCall: ApiCallFn<T>) => {
    setLoading(true);
    setError(null);
    setData(null);

    try {
      const response = await apiCall();
      const wrapper = new ResponseWrapper(response);
      
      if (wrapper.isSuccess()) {
        setData(wrapper.getData() as T);
      } else {
        setError(wrapper.getMessage() || 'An error occurred');
      }
    } catch (err: any) {
      setError(err.message || 'Network error occurred');
      console.error('API Error:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  return { data, loading, error, execute };
};