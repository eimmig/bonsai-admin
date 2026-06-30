import { useMemo, useState } from 'react';

import { type OrderListFilters } from '@/api/orders';
import { CustomSelect } from '@/components/CustomSelect';
import { EmptyState } from '@/components/EmptyState';
import { Pagination } from '@/components/Pagination';
import type { CustomerOption } from '@/hooks/useOrders';
import { currency, dateTime, statusLabel, statusOptions } from '@/lib/format';
import type { OrderResponse, OrderStatus } from '@/types';

interface OrdersViewProps {
  orders: OrderResponse[];
  page: number;
  totalPages: number;
  totalElements: number;
  onPageChange: (page: number) => void;
  orderFilters: OrderListFilters;
  setOrderFilters: React.Dispatch<React.SetStateAction<OrderListFilters>>;
  filterCustomerSearch: string;
  setFilterCustomerSearch: (v: string) => void;
  customerOptions: CustomerOption[];
  selectedOrderId: string;
  setSelectedOrderId: (id: string) => void;
  selectedOrderStatus: OrderStatus;
  setSelectedOrderStatus: (s: OrderStatus) => void;
  selectedHistory: Record<string, string>;
  handleApplyOrderFilters: (e: React.FormEvent<HTMLFormElement>) => void;
  handleResetOrderFilters: (onRefreshAfter: () => void) => void;
  handleUpdateOrderStatus: (e: React.FormEvent<HTMLFormElement>) => void;
  handleLoadHistory: (orderId: string) => void;
  onRefresh: () => void;
}

export function OrdersView({
  orders,
  page,
  totalPages,
  totalElements,
  onPageChange,
  orderFilters,
  setOrderFilters,
  filterCustomerSearch,
  setFilterCustomerSearch,
  customerOptions,
  selectedOrderId,
  setSelectedOrderId,
  selectedOrderStatus,
  setSelectedOrderStatus,
  selectedHistory,
  handleApplyOrderFilters,
  handleResetOrderFilters,
  handleUpdateOrderStatus,
  handleLoadHistory,
  onRefresh,
}: OrdersViewProps) {
  const filteredCustomers = useMemo(() => {
    if (!filterCustomerSearch) return customerOptions;
    return customerOptions.filter((c) =>
      c.email.toLowerCase().includes(filterCustomerSearch.toLowerCase()),
    );
  }, [filterCustomerSearch, customerOptions]);

  const [customerDropdownOpen, setCustomerDropdownOpen] = useState(false);

  return (
    <>
      <div className="grid grid-cols-2 items-start gap-5 max-[900px]:grid-cols-1">
        <div className="card">
          <h2 className="card-title">Filters</h2>
          <form onSubmit={handleApplyOrderFilters} className="flex flex-col gap-3.5">
            <div className="form-field">
              <span className="form-label">Status</span>
              <CustomSelect
                value={orderFilters.status ?? ''}
                onChange={(v) =>
                  setOrderFilters((cur) => ({ ...cur, status: v as OrderStatus | '' }))
                }
                placeholder="All"
                options={[
                  { value: '', label: 'All' },
                  ...statusOptions.map((s) => ({ value: s, label: statusLabel(s) })),
                ]}
              />
            </div>
            <label className="form-field">
              <span className="form-label">Customer</span>
              <input
                className="form-input"
                value={filterCustomerSearch}
                onChange={(e) => {
                  setFilterCustomerSearch(e.target.value);
                  setCustomerDropdownOpen(true);
                  if (!e.target.value) setOrderFilters((cur) => ({ ...cur, customerId: '' }));
                }}
                onFocus={() => setCustomerDropdownOpen(true)}
                onBlur={() => setTimeout(() => setCustomerDropdownOpen(false), 150)}
                placeholder="Search by email..."
                autoComplete="off"
              />
              {customerDropdownOpen && filterCustomerSearch && (
                <div className="absolute left-0 right-0 top-[calc(100%+2px)] z-50 overflow-hidden rounded-[10px] border border-accent/30 bg-input shadow-[0_8px_24px_rgba(0,0,0,0.4)]">
                  {filteredCustomers.slice(0, 8).map((c) => (
                    <button
                      key={c.id}
                      type="button"
                      className="flex w-full items-center justify-between px-3.5 py-2.5 text-left text-[0.85rem] text-main transition-colors hover:bg-accent/10"
                      onMouseDown={(e) => {
                        e.preventDefault();
                        setFilterCustomerSearch(c.email);
                        setOrderFilters((cur) => ({ ...cur, customerId: c.id }));
                        setCustomerDropdownOpen(false);
                      }}
                    >
                      <span>{c.email}</span>
                    </button>
                  ))}
                  {filteredCustomers.length === 0 && (
                    <p className="px-3.5 py-3 text-[0.82rem] text-soft">No customers found</p>
                  )}
                </div>
              )}
            </label>
            <label className="form-field">
              <span className="form-label">Start date</span>
              <input
                type="datetime-local"
                className="form-input"
                value={orderFilters.dateFrom ?? ''}
                onChange={(e) =>
                  setOrderFilters((cur) => ({ ...cur, dateFrom: e.target.value }))
                }
              />
            </label>
            <label className="form-field">
              <span className="form-label">End date</span>
              <input
                type="datetime-local"
                className="form-input"
                value={orderFilters.dateTo ?? ''}
                onChange={(e) =>
                  setOrderFilters((cur) => ({ ...cur, dateTo: e.target.value }))
                }
              />
            </label>
            <div className="flex gap-2.5">
              <button type="submit" className="btn-primary">
                Apply
              </button>
              <button
                type="button"
                className="btn-secondary"
                onClick={() => handleResetOrderFilters(onRefresh)}
              >
                Clear
              </button>
            </div>
          </form>
        </div>

        <div className="card">
          <h2 className="card-title">Update status</h2>
          {orders.length === 0 ? (
            <p className="text-[0.85rem] text-soft">No orders available to update.</p>
          ) : (
            <>
              <form onSubmit={handleUpdateOrderStatus} className="flex flex-col gap-3.5">
                <div className="form-field">
                  <span className="form-label">Order</span>
                  <CustomSelect
                    value={selectedOrderId}
                    onChange={(v) => {
                      const order = orders.find((o) => o.id === v);
                      setSelectedOrderId(v);
                      if (order) setSelectedOrderStatus(order.status);
                    }}
                    searchable
                    options={orders.map((o) => ({
                      value: o.id,
                      label: o.customerName ?? o.customerEmail ?? o.id.slice(0, 8),
                      sublabel: `${statusLabel(o.status)} · ${o.id.slice(0, 8)}`,
                    }))}
                  />
                </div>
                <div className="form-field">
                  <span className="form-label">New status</span>
                  <CustomSelect
                    value={selectedOrderStatus}
                    onChange={(v) => setSelectedOrderStatus(v as OrderStatus)}
                    options={statusOptions.map((s) => ({ value: s, label: statusLabel(s) }))}
                  />
                </div>
                <button type="submit" className="btn-primary">
                  Save status
                </button>
              </form>

              <button
                type="button"
                className="btn-secondary mt-3 w-full"
                onClick={() => void handleLoadHistory(selectedOrderId)}
              >
                View history
              </button>

              {selectedOrderId in selectedHistory && (
                <div className="preview-box mt-3">
                  <p className="preview-label">History</p>
                  {selectedHistory[selectedOrderId] ? (
                    <pre className="whitespace-pre-wrap text-[0.8rem] text-soft">
                      {selectedHistory[selectedOrderId]}
                    </pre>
                  ) : (
                    <span className="text-[0.82rem] text-soft">
                      No status changes recorded for this order.
                    </span>
                  )}
                </div>
              )}
            </>
          )}
        </div>
      </div>

      <div className="card">
        <h2 className="card-title">Order list</h2>
        {orders.length === 0 ? (
          <EmptyState message="No orders found for the applied filters." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full border-collapse">
              <thead>
                <tr>
                  <th className="table-th">ID</th>
                  <th className="table-th">Status</th>
                  <th className="table-th">Items</th>
                  <th className="table-th">Total</th>
                  <th className="table-th">Created at</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order: OrderResponse) => (
                  <tr
                    key={order.id}
                    className={`cursor-pointer transition-colors [&>td]:hover:bg-white/2 ${
                      order.id === selectedOrderId ? '[&>td]:bg-accent/6' : ''
                    }`}
                    onClick={() => {
                      setSelectedOrderId(order.id);
                      setSelectedOrderStatus(order.status);
                    }}
                  >
                    <td className="table-td">
                      <code className="rounded-md bg-white/7 px-1.5 py-px font-mono text-[0.82rem]">
                        {order.id.slice(0, 8)}
                      </code>
                    </td>
                    <td className="table-td">
                      <span className="badge badge-blue">{statusLabel(order.status)}</span>
                    </td>
                    <td className="table-td">{order.items.length}</td>
                    <td className="table-td">
                      {currency(
                        order.items.reduce(
                          (acc, item) => acc + item.unitPrice * item.quantity,
                          0,
                        ),
                      )}
                    </td>
                    <td className="table-td">{dateTime(order.createdAt)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <Pagination
          page={page}
          totalPages={totalPages}
          totalElements={totalElements}
          onPageChange={onPageChange}
        />
      </div>
    </>
  );
}
