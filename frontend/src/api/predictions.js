import client from './client';

export const predictionApi = {
  create: (data) => client.post('/predictions', data),

  getMyPredictions: (params) =>
    client.get('/predictions', { params }),

  getPrediction: (id) =>
    client.get(`/predictions/${id}`),

  deletePrediction: (id) =>
    client.delete(`/predictions/${id}`),

  getReceivedPredictions: (params) =>
    client.get('/predictions/received', { params }),

  // 공개 API (인증 불필요)
  getPublicPrediction: (accessCode) =>
    client.get(`/public/predictions/${accessCode}`),
};

export const attachmentApi = {
  upload: (predictionId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return client.post(`/predictions/${predictionId}/attachments`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  delete: (attachmentId) =>
    client.delete(`/attachments/${attachmentId}`),
};

export const claimApi = {
  create: (predictionId, data) =>
    client.post(`/predictions/${predictionId}/claims`, data),

  getReceivedClaims: (params) =>
    client.get('/claims/received', { params }),

  updateClaim: (claimId, data) =>
    client.put(`/claims/${claimId}`, data),
};
