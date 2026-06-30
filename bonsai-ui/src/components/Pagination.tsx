import { ChevronLeft, ChevronRight } from 'lucide-react';

interface PaginationProps {
  page: number;
  totalPages: number;
  totalElements: number;
  onPageChange: (page: number) => void;
}

export function Pagination({ page, totalPages, totalElements, onPageChange }: PaginationProps) {
  if (totalElements === 0) return null;

  return (
    <div className="flex items-center justify-between border-t border-white/8 pt-3 text-[0.82rem] text-soft">
      <span>
        Page {page + 1} of {Math.max(totalPages, 1)} · {totalElements}{' '}
        {totalElements === 1 ? 'record' : 'records'}
      </span>
      <div className="flex items-center gap-2">
        <button
          type="button"
          className="flex items-center gap-1 rounded-lg border border-white/8 px-2.5 py-1.5 transition-colors hover:border-white/[0.16] hover:bg-white/[0.06] disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:bg-transparent"
          onClick={() => onPageChange(page - 1)}
          disabled={page <= 0}
        >
          <ChevronLeft size={14} />
          Previous
        </button>
        <button
          type="button"
          className="flex items-center gap-1 rounded-lg border border-white/8 px-2.5 py-1.5 transition-colors hover:border-white/[0.16] hover:bg-white/[0.06] disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:bg-transparent"
          onClick={() => onPageChange(page + 1)}
          disabled={page + 1 >= totalPages}
        >
          Next
          <ChevronRight size={14} />
        </button>
      </div>
    </div>
  );
}
