import { useEffect, useMemo, useRef, useState } from 'react';

import { getOrderHistory, listOrders, updateOrderStatus, type OrderListFilters } from '@/api/orders';
import { confirm } from '@/components/ConfirmDialog';
import { extractErrorMessage } from '@/lib/error';
import { dateTime, statusLabel } from '@/lib/format';
import type { OrderResponse, OrderStatus, PageResponse } from '@/types';

export interface CustomerOption {
  id: string;
  email: string;
}

export function useOrders(
  ordersPage: PageResponse<OrderResponse> | null,
  onError: (msg: string) => void,
  onMessage: (msg: string) => void,
  onRefresh: () => void,
) {
  const [page, setPage] = useState(0);
  const [pagedOrders, setPagedOrders] = useState<PageResponse<OrderResponse> | null>(ordersPage);

  useEffect(() => {
    setPagedOrders(ordersPage);
    setPage(0);
  }, [ordersPage]);

  const orders = pagedOrders?.content ?? [];

  const [orderFilters, setOrderFilters] = useState<OrderListFilters>({
    status: '',
    customerId: '',
    dateFrom: '',
    dateTo: '',
  });
  const [filterCustomerSearch, setFilterCustomerSearch] = useState('');
  const [selectedOrderId, setSelectedOrderId] = useState('');
  const [selectedOrderStatus, setSelectedOrderStatus] = useState<OrderStatus>('AGUARDANDO_PAGAMENTO');
  const [selectedHistory, setSelectedHistory] = useState<Record<string, string>>({});

  const selectedOrder = useMemo(
    () => orders.find((o) => o.id === selectedOrderId) ?? null,
    [selectedOrderId, orders],
  );

  useEffect(() => {
    if (orders.length > 0 && !selectedOrderId) {
      setSelectedOrderId(orders[0].id);
      setSelectedOrderStatus(orders[0].status);
    }
  }, [orders, selectedOrderId]);

  // Customer list for the search filter: loaded once, independently of the
  // orders page shown in the table. Without this, the dropdown would only see
  // the customers present on the current page (10 orders) instead of all
  // customers that have ever placed an order.
  const [customerOptions, setCustomerOptions] = useState<CustomerOption[]>([]);
  const [allOrders, setAllOrders] = useState<OrderResponse[]>([]);
  const customerOptionsFetched = useRef(false);

  useEffect(() => {
    if (!ordersPage || customerOptionsFetched.current) return;
    customerOptionsFetched.current = true;
    listOrders({}, 0, 200)
      .then((data) => {
        setAllOrders(data.content);
        const seen = new Set<string>();
        const options: CustomerOption[] = [];
        for (const o of data.content) {
          if (!seen.has(o.customerId) && o.customerEmail) {
            seen.add(o.customerId);
            options.push({ id: o.customerId, email: o.customerEmail });
          }
        }
        setCustomerOptions(options);
      })
      .catch((err: unknown) => {
        console.error(err);
      });
  }, [ordersPage]);

  function handleApplyOrderFilters(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    onRefresh();
  }

  function handleResetOrderFilters(onRefreshAfter: () => void) {
    setOrderFilters({ status: '', customerId: '', dateFrom: '', dateTo: '' });
    setFilterCustomerSearch('');
    setTimeout(() => onRefreshAfter(), 0);
  }

  async function handlePageChange(newPage: number) {
    try {
      const data = await listOrders(orderFilters, newPage);
      setPagedOrders(data);
      setPage(newPage);
    } catch (err) {
      onError(extractErrorMessage(err, 'Could not load the orders page.'));
      console.error(err);
    }
  }

  async function handleUpdateOrderStatus(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (!selectedOrderId) {
      onError('Select an order.');
      return;
    }
    if (!await confirm('Update status', `Confirm changing the status to "${statusLabel(selectedOrderStatus)}"?`)) {
      return;
    }
    try {
      await updateOrderStatus(selectedOrderId, selectedOrderStatus);
      onMessage('Status updated.');
      onRefresh();
    } catch (err) {
      onError(extractErrorMessage(err, 'Could not update the status.'));
      console.error(err);
    }
  }

  async function handleLoadHistory(orderId: string) {
    try {
      const history = await getOrderHistory(orderId);
      setSelectedHistory((cur) => ({
        ...cur,
        [orderId]: history
          .map((entry) => {
            const from = entry.fromStatus ? statusLabel(entry.fromStatus) : 'Start';
            return `${from} → ${statusLabel(entry.toStatus)} by ${entry.changedBy} on ${dateTime(entry.changedAt)}`;
          })
          .join('\n'),
      }));
    } catch (err) {
      onError(extractErrorMessage(err, 'Could not load the history.'));
      console.error(err);
    }
  }

  return {
    orders,
    allOrders,
    page,
    totalPages: pagedOrders?.totalPages ?? 1,
    totalElements: pagedOrders?.totalElements ?? 0,
    handlePageChange,
    orderFilters,
    setOrderFilters,
    filterCustomerSearch,
    setFilterCustomerSearch,
    customerOptions,
    selectedOrderId,
    setSelectedOrderId,
    selectedOrderStatus,
    setSelectedOrderStatus,
    selectedOrder,
    selectedHistory,
    handleApplyOrderFilters,
    handleResetOrderFilters,
    handleUpdateOrderStatus,
    handleLoadHistory,
  };
}
