import axios from 'axios';

const http = axios.create({
  baseURL: import.meta.env.VITE_REMOTE_API
});


export default {

    getAllPublicCollections() {
        return http.get('http://localhost:9000/collections/all-public');
    },

    getCollectionById(id) {
        return http.get(`http://localhost:9000/collections/${id}`);
    },

    getCardsInCollection(id) {
        return http.get(`http://localhost:9000/collections/${id}/cards`);
    },

    getCollectionIdByUser(id) {
        return http.get(`http://localhost:9000/collections/user/${id}`);
    },

    addCardToCollection(request) {
        return http.put(`http://localhost:9000/collections/add`, request);
    },

    removeCardFromCollection(request) {
        return http.delete(`http://localhost:9000/collections/remove`, {data : request});
    },

    removeAllFromCollection(request) {
        return http.delete(`http://localhost:9000/collections/remove-all`, {data : request});
    },

    setCollectionPublic(id) {
        return http.put(`http://localhost:9000/collections/${id}/set-public`);
    },

    setCollectionPrivate(id) {
        return http.put(`http://localhost:9000/collections/${id}/set-private`);
    },
    
    renameCollection(id, collectionName) {
        return http.put(`http://localhost:9000/collections/${id}/rename?collectionName=${collectionName}`);
    },

    getCollectionStats(id) {
        return http.get(`http://localhost:9000/collections/${id}/stats`);
    },
    
    getCardCount(id, cardId) {
        return http.get(`http://localhost:9000/collections/${id}/card-count?cardId=${cardId}`);
    },

    setCollectionThumbnail(id, thumbnailUrlDto) {
        return http.put(`http://localhost:9000/collections/${id}/set-thumbnail`, thumbnailUrlDto);
    },

    getCollectionThumbnail(id) {
        return http.get(`http://localhost:9000/collections/${id}/get-thumbnail`);
    }

}