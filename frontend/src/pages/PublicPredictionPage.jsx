import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { predictionApi } from '../api/predictions';
import { formatDate, formatDateTime, getDaysUntil } from '../utils/date';

export default function PublicPredictionPage() {
  const { accessCode } = useParams();
  const [prediction, setPrediction] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchPrediction();
  }, [accessCode]);

  const fetchPrediction = async () => {
    try {
      const response = await predictionApi.getPublicPrediction(accessCode);
      setPrediction(response.data);
    } catch (err) {
      setError(err.response?.data?.message || '예측을 찾을 수 없습니다.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">오류</h1>
          <p className="text-gray-600">{error}</p>
        </div>
      </div>
    );
  }

  const isPending = prediction.status === 'PENDING';

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="max-w-4xl mx-auto px-4 py-4">
          <a href="/" className="text-2xl font-bold text-primary-600">
            TimeCapsule
          </a>
        </div>
      </header>

      <main className="max-w-2xl mx-auto px-4 py-12">
        {isPending ? (
          // 공개 전
          <div className="bg-white p-8 rounded-lg shadow-sm text-center">
            <div className="w-20 h-20 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <svg
                className="w-10 h-10 text-yellow-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                />
              </svg>
            </div>
            <h1 className="text-2xl font-bold text-gray-900 mb-4">
              아직 공개되지 않았습니다
            </h1>
            <p className="text-gray-600 mb-6">
              이 예측은 아직 공개일이 되지 않았습니다.
            </p>
            <div className="bg-gray-50 p-4 rounded-lg inline-block">
              <p className="text-sm text-gray-500 mb-1">공개 예정일</p>
              <p className="text-xl font-semibold text-gray-900">
                {formatDate(prediction.releaseDate)}
              </p>
              <p className="text-primary-600 mt-1">
                D-{getDaysUntil(prediction.releaseDate)}
              </p>
            </div>
            <div className="mt-6 text-gray-500">
              <p>작성자: {prediction.author?.nickname}</p>
            </div>
          </div>
        ) : (
          // 공개됨
          <div className="bg-white p-8 rounded-lg shadow-sm">
            <div className="text-center mb-8">
              <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg
                  className="w-10 h-10 text-green-600"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 19v-8.93a2 2 0 01.89-1.664l7-4.666a2 2 0 012.22 0l7 4.666A2 2 0 0121 10.07V19M3 19a2 2 0 002 2h14a2 2 0 002-2M3 19l6.75-4.5M21 19l-6.75-4.5M3 10l6.75 4.5M21 10l-6.75 4.5m0 0l-1.14.76a2 2 0 01-2.22 0l-1.14-.76"
                  />
                </svg>
              </div>
              <p className="text-gray-600">
                {prediction.author?.nickname}님이 남긴 예측
              </p>
            </div>

            <h1 className="text-2xl font-bold text-gray-900 mb-4 text-center">
              {prediction.title}
            </h1>

            <div className="border-t border-b py-6 my-6">
              <div className="whitespace-pre-wrap text-gray-800">
                {prediction.content}
              </div>
            </div>

            {/* Attachments */}
            {prediction.attachments && prediction.attachments.length > 0 && (
              <div className="mb-6">
                <h2 className="text-sm font-medium text-gray-500 mb-2">첨부파일</h2>
                <ul className="space-y-2">
                  {prediction.attachments.map((attachment) => (
                    <li key={attachment.id}>
                      <a
                        href={`http://localhost:8080${attachment.downloadUrl}`}
                        className="text-primary-600 hover:underline"
                        target="_blank"
                        rel="noopener noreferrer"
                      >
                        {attachment.originalName}
                      </a>
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {/* Proof */}
            <div className="bg-green-50 p-4 rounded-lg text-center">
              <p className="text-green-800 font-medium">
                {prediction.proofMessage}
              </p>
              <div className="mt-3 text-sm text-green-600">
                <p>작성일: {formatDateTime(prediction.createdAt)}</p>
                <p>공개일: {formatDate(prediction.releaseDate)}</p>
              </div>
            </div>
          </div>
        )}
      </main>

      {/* Footer */}
      <footer className="py-6 text-center text-gray-500 text-sm">
        <a href="/" className="hover:text-primary-600">
          TimeCapsule에서 나만의 예측 작성하기
        </a>
      </footer>
    </div>
  );
}
