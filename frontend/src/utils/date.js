import { format, parseISO, isAfter, isBefore, isToday } from 'date-fns';
import { ko } from 'date-fns/locale';

export function formatDate(dateString, formatStr = 'yyyy년 M월 d일') {
  if (!dateString) return '';
  const date = typeof dateString === 'string' ? parseISO(dateString) : dateString;
  return format(date, formatStr, { locale: ko });
}

export function formatDateTime(dateString) {
  if (!dateString) return '';
  const date = typeof dateString === 'string' ? parseISO(dateString) : dateString;
  return format(date, 'yyyy년 M월 d일 HH:mm', { locale: ko });
}

export function isReleased(releaseDate) {
  if (!releaseDate) return false;
  const date = typeof releaseDate === 'string' ? parseISO(releaseDate) : releaseDate;
  return !isBefore(new Date(), date);
}

export function getDaysUntil(dateString) {
  if (!dateString) return 0;
  const date = typeof dateString === 'string' ? parseISO(dateString) : dateString;
  const now = new Date();
  const diffTime = date.getTime() - now.getTime();
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
}
