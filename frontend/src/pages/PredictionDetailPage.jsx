import { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import Layout from '../components/Layout';
import Button from '../components/Button';
import { predictionApi } from '../api/predictions';
import { formatDate, formatDateTime } from '../utils/date';
import client from '../api/client';

export default function PredictionDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const [prediction, setPrediction] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showCopied, setShowCopied] = useState(false);

  useEffect(() => {
    fetchPrediction();
  }, [id]);

  const fetchPrediction = async () => {
    try {
      const response = await predictionApi.getPrediction(id);
      setPrediction(response.data);
    } catch (error) {
      console.error('Failed to fetch prediction:', error);
      navigate('/dashboard');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('정말로 이 예측을 삭제하시겠습니까?')) return;

    try {
      await predictionApi.deletePrediction(id);
      navigate('/dashboard', { state: { message: '예측이 삭제되었습니다.' } });
    } catch (error) {
      console.error('Failed to delete prediction:', error);
    }
  };

  const copyLink = () => {
    const url = prediction?.recipient?.accessUrl;
    if (url) {
      navigator.clipboard.writeText(url);
      setShowCopied(true);
      setTimeout(() => setShowCopied(false), 2000);
    }
  };

  const downloadFile = async (attachment) => {
    try {
      const response = await client.get(attachment.downloadUrl, {
        responseType: 'blob',
      });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', attachment.originalName);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Failed to download file:', error);
      alert('파일 다운로드에 실패했습니다.');
    }
  };

  if (loading) {
    return (
      <Layout>
        <div className="min-h-[60vh] flex items-center justify-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      </Layout>
    );
  }

  if (!prediction) return null;

  const isReleased = prediction.status === 'RELEASED';

  return (
    <Layout>
      <div className="max-w-2xl mx-auto px-4 py-8">
        {location.state?.message && (
          <div className="mb-4 p-3 bg-green-50 text-green-600 rounded-lg">
            {location.state.message}
          </div>
        )}

        <div className="bg-white p-6 rounded-lg shadow-sm">
          {/* Header */}
          <div className="flex justify-between items-start mb-6">
            <div>
              <h1 className="text-2xl font-bold text-gray-900 mb-2">
                {prediction.title}
              </h1>
              <p className="text-sm text-gray-500">
                작성일: {formatDateTime(prediction.createdAt)}
              </p>
            </div>
            <span
              className={`px-3 py-1 rounded-full text-sm ${
                isReleased
                  ? 'bg-green-100 text-green-800'
                  : 'bg-yellow-100 text-yellow-800'
              }`}
            >
              {isReleased ? '공개됨' : '공개 전'}
            </span>
          </div>

          {/* Content */}
          <div className="mb-6">
            <h2 className="text-sm font-medium text-gray-500 mb-2">예측 내용</h2>
            <div className="bg-gray-50 p-4 rounded-lg whitespace-pre-wrap">
              {prediction.content}
            </div>
          </div>

          {/* Release Date */}
          <div className="mb-6">
            <h2 className="text-sm font-medium text-gray-500 mb-2">공개일</h2>
            <p className="text-gray-900">{formatDate(prediction.releaseDate)}</p>
          </div>

          {/* Attachments */}
          {prediction.attachments && prediction.attachments.length > 0 && (
            <div className="mb-6">
              <h2 className="text-sm font-medium text-gray-500 mb-2">첨부파일</h2>
              <ul className="space-y-2">
                {prediction.attachments.map((attachment) => (
                  <li key={attachment.id}>
                    <button
                      onClick={() => downloadFile(attachment)}
                      className="text-primary-600 hover:underline"
                    >
                      {attachment.originalName}
                    </button>
                    <span className="text-gray-400 text-sm ml-2">
                      ({Math.round(attachment.fileSize / 1024)}KB)
                    </span>
                  </li>
                ))}
              </ul>
            </div>
          )}

          {/* Recipient Info */}
          {prediction.recipient && (
            <div className="mb-6">
              <h2 className="text-sm font-medium text-gray-500 mb-2">수신자 정보</h2>
              <div className="bg-gray-50 p-4 rounded-lg">
                <p className="text-sm text-gray-600 mb-2">
                  유형: {prediction.recipient.type === 'EMAIL' ? '이메일' : '링크만'}
                </p>
                {prediction.recipient.email && (
                  <p className="text-sm text-gray-600 mb-2">
                    이메일: {prediction.recipient.email}
                  </p>
                )}
                <div className="flex items-center gap-2 mt-3">
                  <input
                    type="text"
                    value={prediction.recipient.accessUrl || ''}
                    readOnly
                    className="flex-1 px-3 py-2 bg-white border rounded text-sm"
                  />
                  <Button variant="secondary" size="sm" onClick={copyLink}>
                    {showCopied ? '복사됨!' : '복사'}
                  </Button>
                </div>
                {prediction.recipient.viewedAt && (
                  <p className="text-sm text-green-600 mt-2">
                    열람됨: {formatDateTime(prediction.recipient.viewedAt)}
                  </p>
                )}
              </div>
            </div>
          )}

          {/* Actions */}
          <div className="flex gap-3 pt-4 border-t">
            <Button
              variant="secondary"
              onClick={() => navigate('/dashboard')}
              className="flex-1"
            >
              목록으로
            </Button>
            <Button
              variant="danger"
              onClick={handleDelete}
              className="flex-1"
            >
              삭제
            </Button>
          </div>
        </div>
      </div>
    </Layout>
  );
}
