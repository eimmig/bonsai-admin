export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  email: string;
  roles: string[];
}

export interface RegisterRequest {
  email: string;
  password: string;
}

export interface UserResponse {
  id: string;
  email: string;
  active: boolean;
  roles: string[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export type OrderStatus = 'AGUARDANDO_PAGAMENTO' | 'PAGO' | 'CANCELADO' | 'EM_TRANSPORTE' | 'ENTREGUE';

export type OrderSummary = Partial<Record<OrderStatus, number>>;

export interface OrderItemResponse {
  id: string;
  productName: string;
  quantity: number;
  unitPrice: number;
}

export interface OrderResponse {
  id: string;
  customerId: string;
  customerEmail: string | null;
  customerName: string | null;
  status: OrderStatus;
  createdAt: string;
  updatedAt: string;
  items: OrderItemResponse[];
}

export interface OrderStatusHistoryResponse {
  fromStatus: OrderStatus | null;
  toStatus: OrderStatus;
  changedBy: string;
  changedAt: string;
}

export interface UpdateOrderStatusRequest {
  status: OrderStatus;
}

export interface AssignRolesRequest {
  roles: string[];
}

export interface AttachmentResponse {
  id: string;
  orderId: string;
  type: string;
  fileName: string;
  mimeType: string;
  sizeBytes: number;
  createdAt: string;
}

export interface CurrentUser {
  email: string;
  roles: string[];
}

export interface DashboardSnapshot {
  users: PageResponse<UserResponse> | null;
  orders: PageResponse<OrderResponse> | null;
  orderSummary: OrderSummary | null;
}
