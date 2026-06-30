import { useMemo, useRef, useState } from 'react';
import { Check, ChevronDown, Search } from 'lucide-react';

export interface SelectOption {
  value: string;
  label: string;
  sublabel?: string;
}

interface CustomSelectProps {
  value: string;
  onChange: (value: string) => void;
  options: SelectOption[];
  placeholder?: string;
  searchable?: boolean;
}

export function CustomSelect({
  value,
  onChange,
  options,
  placeholder = 'Select…',
  searchable = false,
}: CustomSelectProps) {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState('');
  const triggerRef = useRef<HTMLButtonElement>(null);

  const selected = options.find((o) => o.value === value);

  const filtered = useMemo(() => {
    if (!query) return options;
    const q = query.toLowerCase();
    return options.filter(
      (o) =>
        o.label.toLowerCase().includes(q) ||
        (o.sublabel ?? '').toLowerCase().includes(q),
    );
  }, [query, options]);

  function handleSelect(val: string) {
    onChange(val);
    setOpen(false);
    setQuery('');
  }

  function handleTriggerClick() {
    setOpen((prev) => !prev);
    if (!open) setQuery('');
  }

  function handleBlur(e: React.FocusEvent<HTMLDivElement>) {
    if (!e.currentTarget.contains(e.relatedTarget)) {
      setOpen(false);
      setQuery('');
    }
  }

  return (
    <div className="relative" onBlur={handleBlur}>
      <button
        ref={triggerRef}
        type="button"
        className={`form-input flex w-full items-center justify-between gap-2 text-left ${open ? 'border-accent/50' : ''}`}
        onClick={handleTriggerClick}
      >
        <span className={selected ? 'text-main' : 'text-dim'}>
          {selected ? selected.label : placeholder}
        </span>
        <ChevronDown
          size={14}
          className={`shrink-0 text-dim transition-transform ${open ? 'rotate-180' : ''}`}
        />
      </button>

      {open && (
        <div className="absolute left-0 right-0 top-[calc(100%+3px)] z-50 overflow-hidden rounded-[10px] border border-accent/30 bg-input shadow-[0_8px_24px_rgba(0,0,0,0.45)]">
          {searchable && (
            <div className="flex items-center gap-2 border-b border-white/8 px-3 py-2">
              <Search size={13} className="shrink-0 text-dim" />
              <input
                autoFocus
                className="w-full bg-transparent text-[0.85rem] text-main outline-none placeholder:text-dim"
                placeholder="Search…"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
              />
            </div>
          )}

          <div className="max-h-56 overflow-y-auto">
            {filtered.length === 0 ? (
              <p className="px-3.5 py-3 text-[0.82rem] text-soft">No results</p>
            ) : (
              filtered.map((opt) => (
                <button
                  key={opt.value}
                  type="button"
                  className="flex w-full items-center gap-2.5 px-3.5 py-2.5 text-left transition-colors hover:bg-accent/10"
                  onMouseDown={(e) => { e.preventDefault(); handleSelect(opt.value); }}
                >
                  <div className="flex min-w-0 flex-1 flex-col">
                    <span className="truncate text-[0.85rem] text-main">{opt.label}</span>
                    {opt.sublabel && (
                      <span className="truncate text-[0.78rem] text-soft">{opt.sublabel}</span>
                    )}
                  </div>
                  {opt.value === value && (
                    <Check size={13} className="shrink-0 text-accent" />
                  )}
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}
