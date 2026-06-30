import { apiClient } from '@/lib/api-client';

import type { LoginRequest, LoginResponse, RegisterRequest, UserResponse } from '@/types';

export async function login(request: LoginRequest): Promise<LoginResponse> {
  const response = await apiClient.post<LoginResponse>('/auth/login', request);
  return response.data;
}

export async function registerUser(request: RegisterRequest): Promise<UserResponse> {
  const response = await apiClient.post<UserResponse>('/users/register', request);
  return response.data;
}
