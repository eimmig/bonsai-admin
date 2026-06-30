import { useMemo, useState } from 'react';
import { Download, Search } from 'lucide-react';

import { CustomSelect } from '@/components/CustomSelect';
import { EmptyState } from '@/components/EmptyState';
import { attachmentTypeLabel, currency, dateTime, statusLabel } from '@/lib/format';
import type { AttachmentResponse, OrderResponse, OrderStatus } from '@/types';

interface FilesViewProps {
  orders: OrderResponse[];
  allOrders: OrderResponse[];
  selectedOrderId: string;
  setSelectedOrderId: (id: string) => void;
  setSelectedOrderStatus: (s: OrderStatus) => void;
  selectedOrder: OrderResponse | null;
  orderAttachments: AttachmentResponse[];
  attachmentType: string;
  setAttachmentType: (t: string) => void;
  attachmentFile: File | null;
  setAttachmentFile: (f: File | null) => void;
  handleUploadAttachment: (e: React.FormEvent<HTMLFormElement>) => void;
  handleDownloadAttachment: (attachmentId: string, fileName: string) => void;
}

function orderLabel(o: OrderResponse): string {
  const who = o.customerName ?? o.customerEmail ?? o.customerId.slice(0, 8);
  return `${who} — ${statusLabel(o.status)} — ${dateTime(o.createdAt)}`;
}

export function FilesView({
  orders,
  allOrders,
  selectedOrderId,
  setSelectedOrderId,
  setSelectedOrderStatus,
  selectedOrder,
  orderAttachments,
  attachmentType,
  setAttachmentType,
  attachmentFile,
  setAttachmentFile,
  handleUploadAttachment,
  handleDownloadAttachment,
}: FilesViewProps) {
  const [orderSearch, setOrderSearch] = useState('');
  const [dropdownOpen, setDropdownOpen] = useState(false);

  const sourceOrders = allOrders.length > 0 ? allOrders : orders;

  const filteredOrders = useMemo(() => {
    if (!orderSearch) return sourceOrders;
    const term = orderSearch.toLowerCase();
    return sourceOrders.filter(
      (o) =>
        o.id.toLowerCase().includes(term) ||
        (o.customerName ?? '').toLowerCase().includes(term) ||
        (o.customerEmail ?? '').toLowerCase().includes(term) ||
        statusLabel(o.status).toLowerCase().includes(term),
    );
  }, [orderSearch, sourceOrders]);

  const selectedLabel = useMemo(() => {
    const o = sourceOrders.find((x) => x.id === selectedOrderId);
    return o ? orderLabel(o) : '';
  }, [selectedOrderId, sourceOrders]);

  function pickOrder(o: OrderResponse) {
    setSelectedOrderId(o.id);
    setSelectedOrderStatus(o.status);
    setOrderSearch('');
    setDropdownOpen(false);
  }

  return (
    <>
      <div className="grid grid-cols-2 items-start gap-5 max-[900px]:grid-cols-1">
        <div className="card">
          <h2 className="card-title">Upload attachment</h2>
          <form onSubmit={handleUploadAttachment} className="flex flex-col gap-3.5">
            <div className="form-field">
              <span className="form-label">Order</span>
              <div className="relative">
                <Search size={14} className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-dim" />
                <input
                  className="form-input pl-8"
                  placeholder="Search by customer, status…"
                  value={dropdownOpen ? orderSearch : selectedLabel}
                  onChange={(e) => {
                    setOrderSearch(e.target.value);
                    setDropdownOpen(true);
                  }}
                  onFocus={() => {
                    setOrderSearch('');
                    setDropdownOpen(true);
                  }}
                  onBlur={() => setTimeout(() => setDropdownOpen(false), 150)}
                  autoComplete="off"
                />
                {dropdownOpen && (
                  <div className="absolute left-0 right-0 top-[calc(100%+2px)] z-50 max-h-60 overflow-y-auto rounded-[10px] border border-accent/30 bg-input shadow-[0_8px_24px_rgba(0,0,0,0.4)]">
                    {filteredOrders.length === 0 ? (
                      <p className="px-3.5 py-3 text-[0.82rem] text-soft">No orders found</p>
                    ) : (
                      filteredOrders.map((o) => (
                        <button
                          key={o.id}
                          type="button"
                          className={`flex w-full flex-col px-3.5 py-2.5 text-left text-[0.82rem] transition-colors hover:bg-accent/10 ${o.id === selectedOrderId ? 'bg-accent/8' : ''}`}
                          onMouseDown={(e) => { e.preventDefault(); pickOrder(o); }}
                        >
                          <span className="font-medium text-main">
                            {o.customerName ?? o.customerEmail ?? o.id.slice(0, 8)}
                          </span>
                          <span className="text-soft">
                            {o.customerEmail && o.customerName ? `${o.customerEmail} · ` : ''}
                            {statusLabel(o.status)} · {dateTime(o.createdAt)}
                          </span>
                        </button>
                      ))
                    )}
                  </div>
                )}
              </div>
            </div>

            <div className="form-field">
              <span className="form-label">Type</span>
              <CustomSelect
                value={attachmentType}
                onChange={(v) => { setAttachmentType(v); setAttachmentFile(null); }}
                options={[
                  { value: 'NOTA_FISCAL', label: 'Invoice', sublabel: 'PDF only' },
                  { value: 'COMPROVANTE', label: 'Receipt' },
                  { value: 'OUTRO',       label: 'Other' },
                ]}
              />
            </div>
            <label className="form-field">
              <span className="form-label">
                File
                {attachmentType === 'NOTA_FISCAL' && (
                  <span className="ml-1.5 font-normal text-soft"> — PDF only</span>
                )}
              </span>
              <input
                type="file"
                key={attachmentType}
                accept={attachmentType === 'NOTA_FISCAL' ? 'application/pdf' : undefined}
                onChange={(e) => setAttachmentFile(e.target.files?.[0] ?? null)}
              />
              {attachmentFile &&
                attachmentType === 'NOTA_FISCAL' &&
                attachmentFile.type !== 'application/pdf' && (
                  <span className="text-[0.8rem] text-[#f87171]">
                    ⚠ This type requires a PDF file.
                  </span>
                )}
            </label>
            <button type="submit" className="btn-primary">
              Upload attachment
            </button>
          </form>
        </div>

        <div className="card">
          <h2 className="card-title">Selected order</h2>
          {selectedOrder ? (
            <div className="preview-box">
              <p className="preview-label">Customer</p>
              <span className="text-[0.9rem] font-medium text-main">
                {selectedOrder.customerName ?? selectedOrder.customerEmail ?? '—'}
              </span>
              {selectedOrder.customerName && selectedOrder.customerEmail && (
                <span className="text-[0.8rem] text-soft">{selectedOrder.customerEmail}</span>
              )}
              <p className="preview-label mt-2.5">Status</p>
              <span className="badge badge-blue">{statusLabel(selectedOrder.status)}</span>
              <p className="preview-label mt-2.5">Created at</p>
              <span className="text-[0.82rem] text-soft">{dateTime(selectedOrder.createdAt)}</span>
              <p className="preview-label mt-2.5">Items</p>
              {selectedOrder.items.map((item) => (
                <span key={item.id} className="block text-[0.82rem] text-soft">
                  {item.quantity}× {item.productName} — {currency(item.unitPrice * item.quantity)}
                </span>
              ))}
            </div>
          ) : (
            <p className="text-[0.85rem] text-soft">No order selected.</p>
          )}
        </div>
      </div>

      <div className="card">
        <h2 className="card-title">Order attachments</h2>
        {orderAttachments.length === 0 ? (
          <EmptyState message="No attachments found for this order." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full border-collapse">
              <thead>
                <tr>
                  <th className="table-th">File</th>
                  <th className="table-th">Type</th>
                  <th className="table-th">Size</th>
                  <th className="table-th">Uploaded at</th>
                  <th className="table-th">Action</th>
                </tr>
              </thead>
              <tbody>
                {orderAttachments.map((att) => (
                  <tr key={att.id}>
                    <td className="table-td">
                      <code className="rounded-md bg-white/7 px-1.5 py-px font-mono text-[0.82rem]">
                        {att.fileName}
                      </code>
                    </td>
                    <td className="table-td">
                      <span className="badge badge-blue">{attachmentTypeLabel(att.type)}</span>
                    </td>
                    <td className="table-td">{(att.sizeBytes / 1024).toFixed(1)} KB</td>
                    <td className="table-td">{dateTime(att.createdAt)}</td>
                    <td className="table-td">
                      <button
                        type="button"
                        className="btn-link inline-flex items-center gap-1"
                        onClick={() => void handleDownloadAttachment(att.id, att.fileName)}
                      >
                        <Download size={14} />
                        Download
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  );
}
