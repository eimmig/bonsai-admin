import { useEffect, useState } from 'react';

import { downloadAttachment, listAttachments, uploadAttachment } from '@/api/attachments';
import { extractBlobErrorMessage, extractErrorMessage } from '@/lib/error';
import type { AttachmentResponse } from '@/types';

export function useAttachments(
  selectedOrderId: string,
  token: string | null,
  onError: (msg: string) => void,
  onMessage: (msg: string) => void,
) {
  const [orderAttachments, setOrderAttachments] = useState<AttachmentResponse[]>([]);
  const [attachmentType, setAttachmentType] = useState('NOTA_FISCAL');
  const [attachmentFile, setAttachmentFile] = useState<File | null>(null);

  useEffect(() => {
    if (!selectedOrderId || !token) return;
    listAttachments(selectedOrderId)
      .then(setOrderAttachments)
      .catch((err: unknown) => {
        setOrderAttachments([]);
        onError(extractErrorMessage(err, 'Could not load the attachments for this order.'));
        console.error(err);
      });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedOrderId, token]);

  async function handleUploadAttachment(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (!selectedOrderId || !attachmentFile) {
      onError('Select an order and a file.');
      return;
    }
    try {
      await uploadAttachment(selectedOrderId, attachmentType, attachmentFile);
      onMessage('Attachment uploaded successfully.');
      setAttachmentFile(null);
      const updated = await listAttachments(selectedOrderId);
      setOrderAttachments(updated);
    } catch (err) {
      onError(extractErrorMessage(err, 'Could not upload the attachment.'));
      console.error(err);
    }
  }

  async function handleDownloadAttachment(attachmentId: string, fileName: string) {
    try {
      const blob = await downloadAttachment(attachmentId);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName || 'attachment';
      a.click();
      window.URL.revokeObjectURL(url);
      onMessage('Download started.');
    } catch (err) {
      onError(await extractBlobErrorMessage(err, 'Could not download the attachment.'));
      console.error(err);
    }
  }

  return {
    orderAttachments,
    attachmentType,
    setAttachmentType,
    attachmentFile,
    setAttachmentFile,
    handleUploadAttachment,
    handleDownloadAttachment,
  };
}
