import type { OrderStatus } from '@/types';

export const statusOptions: OrderStatus[] = [
  'AGUARDANDO_PAGAMENTO',
  'PAGO',
  'CANCELADO',
  'EM_TRANSPORTE',
  'ENTREGUE',
];

const statusLabels: Record<OrderStatus, string> = {
  AGUARDANDO_PAGAMENTO: 'Awaiting payment',
  PAGO: 'Paid',
  CANCELADO: 'Cancelled',
  EM_TRANSPORTE: 'In transit',
  ENTREGUE: 'Delivered',
};

const roleLabels: Record<string, string> = {
  ADMIN: 'Admin',
  OPERATOR: 'Operator',
};

const attachmentTypeLabels: Record<string, string> = {
  NOTA_FISCAL: 'Invoice',
  COMPROVANTE: 'Receipt',
  OUTRO: 'Other',
};

export function statusLabel(status: OrderStatus): string {
  return statusLabels[status];
}

export function roleLabel(role: string): string {
  return roleLabels[role] ?? role;
}

export function attachmentTypeLabel(type: string): string {
  return attachmentTypeLabels[type] ?? type;
}

export function currency(value: number): string {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
}

export function dateTime(value: string): string {
  return new Intl.DateTimeFormat('en-US', { dateStyle: 'short', timeStyle: 'short' }).format(
    new Date(value),
  );
}
