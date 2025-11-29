import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '../../core/api/apiClient';

const useApi = () => {
  const queryClient = useQueryClient();

  const get = (url, options = {}) => {
    return useQuery({
      queryKey: [url],
      queryFn: () => apiClient.get(url, options).then(res => res.data),
      ...options
    });
  };

  const post = (mutationOptions = {}) => {
    return useMutation({
      mutationFn: ({ url, data }) => apiClient.post(url, data),
      ...mutationOptions
    });
  };

  const put = (mutationOptions = {}) => {
    return useMutation({
      mutationFn: ({ url, data }) => apiClient.put(url, data),
      ...mutationOptions
    });
  };

  const remove = (mutationOptions = {}) => {
    return useMutation({
      mutationFn: ({ url }) => apiClient.delete(url),
      ...mutationOptions
    });
  };

  return {
    get,
    post,
    put,
    remove,
    invalidateQueries: queryClient.invalidateQueries
  };
};

export default useApi;