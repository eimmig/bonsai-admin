import { apiClient } from '@/lib/api-client';

import type {
  OrderResponse,
  OrderStatus,
  OrderStatusHistoryResponse,
  OrderSummary,
  PageResponse,
  UpdateOrderStatusRequest,
} from '@/types';

export interface OrderListFilters {
  status?: OrderStatus | '';
  customerId?: string;
  dateFrom?: string;
  dateTo?: string;
}

export async function listOrderCustomerIds(): Promise<string[]> {
  const response = await apiClient.get<string[]>('/orders/customers');
  return response.data;
}

export async function listOrders(
  filters: OrderListFilters = {},
  page = 0,
  size = 10,
): Promise<PageResponse<OrderResponse>> {
  const response = await apiClient.get<PageResponse<OrderResponse>>('/orders', {
    params: {
      status: filters.status || undefined,
      customerId: filters.customerId || undefined,
      dateFrom: filters.dateFrom || undefined,
      dateTo: filters.dateTo || undefined,
      page,
      size,
    },
  });
  return response.data;
}

export async function getOrderSummary(): Promise<OrderSummary> {
  const response = await apiClient.get<OrderSummary>('/orders/summary');
  return response.data;
}

export async function updateOrderStatus(id: string, status: OrderStatus): Promise<OrderResponse> {
  const payload: UpdateOrderStatusRequest = { status };
  const response = await apiClient.put<OrderResponse>(`/orders/${id}/status`, payload);
  return response.data;
}

export async function getOrderHistory(id: string): Promise<OrderStatusHistoryResponse[]> {
  const response = await apiClient.get<OrderStatusHistoryResponse[]>(`/orders/${id}/history`);
  return response.data;
}
