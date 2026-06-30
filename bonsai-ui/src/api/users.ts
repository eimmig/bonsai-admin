import { apiClient } from '@/lib/api-client';

import type { AssignRolesRequest, PageResponse, UserResponse } from '@/types';

export async function listUsers(page = 0, size = 10): Promise<PageResponse<UserResponse>> {
  const response = await apiClient.get<PageResponse<UserResponse>>('/users', {
    params: { page, size },
  });
  return response.data;
}

export async function activateUser(id: string): Promise<UserResponse> {
  const response = await apiClient.put<UserResponse>(`/users/${id}/activate`);
  return response.data;
}

export async function assignRoles(id: string, roles: string[]): Promise<UserResponse> {
  const payload: AssignRolesRequest = { roles };
  const response = await apiClient.put<UserResponse>(`/users/${id}/roles`, payload);
  return response.data;
}
