import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../components/Layout';
import { predictionApi } from '../api/predictions';
import { formatDate, getDaysUntil } from '../utils/date';

export default function DashboardPage() {
  const [tab, setTab] = useState('sent');
  const [predictions, setPredictions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPredictions();
  }, [tab]);

  const fetchPredictions = async () => {
    setLoading(true);
    try {
      const response = tab === 'sent'
        ? await predictionApi.getMyPredictions({ size: 20 })
        : await predictionApi.getReceivedPredictions({ size: 20 });
      setPredictions(response.data.content || []);
    } catch (error) {
      console.error('Failed to fetch predictions:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="max-w-4xl mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-2xl font-bold text-gray-900">대시보드</h1>
          <Link
            to="/predictions/new"
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700"
          >
            새 예측 작성
          </Link>
        </div>

        {/* Tabs */}
        <div className="flex border-b mb-6">
          <button
            onClick={() => setTab('sent')}
            className={`px-4 py-2 font-medium border-b-2 transition ${
              tab === 'sent'
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            보낸 예측
          </button>
          <button
            onClick={() => setTab('received')}
            className={`px-4 py-2 font-medium border-b-2 transition ${
              tab === 'received'
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            받은 예측
          </button>
        </div>

        {/* Predictions List */}
        {loading ? (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          </div>
        ) : predictions.length === 0 ? (
          <div className="text-center py-12 bg-gray-50 rounded-lg">
            <p className="text-gray-500 mb-4">
              {tab === 'sent' ? '아직 작성한 예측이 없습니다.' : '받은 예측이 없습니다.'}
            </p>
            {tab === 'sent' && (
              <Link
                to="/predictions/new"
                className="text-primary-600 hover:underline"
              >
                첫 번째 예측 작성하기
              </Link>
            )}
          </div>
        ) : (
          <div className="space-y-4">
            {predictions.map((prediction) => (
              <PredictionCard
                key={prediction.id}
                prediction={prediction}
                type={tab}
              />
            ))}
          </div>
        )}
      </div>
    </Layout>
  );
}

function PredictionCard({ prediction, type }) {
  const daysUntil = getDaysUntil(prediction.releaseDate);
  const isReleased = prediction.status === 'RELEASED';

  return (
    <Link
      to={`/predictions/${prediction.id}`}
      className="block bg-white p-6 rounded-lg shadow-sm hover:shadow-md transition"
    >
      <div className="flex justify-between items-start">
        <div className="flex-1">
          <h3 className="font-semibold text-gray-900 mb-1">
            {isReleased || type === 'sent' ? prediction.title : '(공개 전)'}
          </h3>
          <p className="text-sm text-gray-500">
            공개일: {formatDate(prediction.releaseDate)}
          </p>
          {type === 'sent' && prediction.recipientEmail && (
            <p className="text-sm text-gray-500 mt-1">
              수신자: {prediction.recipientEmail}
            </p>
          )}
          {type === 'received' && prediction.author && (
            <p className="text-sm text-gray-500 mt-1">
              작성자: {prediction.author.nickname}
            </p>
          )}
        </div>
        <div className="ml-4">
          {isReleased ? (
            <span className="inline-block px-3 py-1 bg-green-100 text-green-800 text-sm rounded-full">
              공개됨
            </span>
          ) : (
            <span className="inline-block px-3 py-1 bg-yellow-100 text-yellow-800 text-sm rounded-full">
              D-{daysUntil}
            </span>
          )}
        </div>
      </div>
      {prediction.viewed !== undefined && (
        <div className="mt-2 text-sm text-gray-500">
          {prediction.viewed ? '열람됨' : '미열람'}
        </div>
      )}
    </Link>
  );
}
