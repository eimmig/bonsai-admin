import { statusLabel, statusOptions } from '@/lib/format';
import type { DashboardSnapshot } from '@/types';

interface OverviewViewProps {
  dashboard: DashboardSnapshot;
  loading: boolean;
}

export function OverviewView({ dashboard, loading }: OverviewViewProps) {
  const orderSummary = dashboard.orderSummary;

  return (
    <>
      <div className="grid grid-cols-3 gap-3 max-[900px]:grid-cols-1">
        <div className="rounded-[14px] border border-white/8 bg-surface px-5 py-4">
          <p className="mb-1.5 text-[0.78rem] uppercase tracking-wide text-soft">Users</p>
          <strong className="text-[1.6rem] font-bold text-bright">
            {dashboard.users?.totalElements ?? 0}
          </strong>
        </div>
        <div className="rounded-[14px] border border-white/8 bg-surface px-5 py-4">
          <p className="mb-1.5 text-[0.78rem] uppercase tracking-wide text-soft">Orders</p>
          <strong className="text-[1.6rem] font-bold text-bright">
            {dashboard.orders?.totalElements ?? 0}
          </strong>
        </div>
        <div className="rounded-[14px] border border-white/8 bg-surface px-5 py-4">
          <p className="mb-1.5 text-[0.78rem] uppercase tracking-wide text-soft">Last refresh</p>
          <strong className="text-[1.6rem] font-bold text-bright">
            {loading ? '…' : new Date().toLocaleTimeString('en-US')}
          </strong>
        </div>
      </div>

      <div className="text-[0.78rem] font-semibold uppercase tracking-wide text-dim">
        Orders by status
      </div>
      <div className="grid grid-cols-5 gap-2.5 max-[900px]:grid-cols-2">
        {statusOptions.map((s) => (
          <div key={s} className="rounded-xl border border-white/8 bg-panel p-3.5">
            <p className="mb-1.5 text-[0.75rem] text-soft">{statusLabel(s)}</p>
            <strong className="text-[1.4rem] font-bold text-bright">{orderSummary?.[s] ?? 0}</strong>
          </div>
        ))}
      </div>
    </>
  );
}
