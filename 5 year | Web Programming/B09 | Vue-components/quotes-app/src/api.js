import axios from 'axios';


const API_KEY = '34894baeba99807896cad774f72ad878';

const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Authorization': `Token token=${API_KEY}`
  }
});

export default apiClient;
