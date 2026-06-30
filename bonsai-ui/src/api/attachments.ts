import { apiClient } from '@/lib/api-client';

import type { AttachmentResponse } from '@/types';

export async function listAttachments(orderId: string): Promise<AttachmentResponse[]> {
  const response = await apiClient.get<AttachmentResponse[]>(`/orders/${orderId}/attachments`);
  return response.data;
}

export async function uploadAttachment(orderId: string, type: string, file: File): Promise<AttachmentResponse> {
  const formData = new FormData();
  formData.append('type', type);
  formData.append('file', file);

  const response = await apiClient.post<AttachmentResponse>(`/orders/${orderId}/attachments`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

  return response.data;
}

export async function downloadAttachment(id: string): Promise<Blob> {
  const response = await apiClient.get(`/attachments/${id}/download`, {
    responseType: 'blob',
  });

  return response.data;
}
