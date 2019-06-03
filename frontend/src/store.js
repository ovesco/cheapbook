/* eslint-disable no-param-reassign */
import Vue from 'vue';
import Vuex from 'vuex';
import Axios from 'axios';

const instance = Axios.create({
    baseURL: 'http://localhost:9000',
});

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        token: null,
    },
    mutations: {
        setToken(state, token) {
            state.token = token;
        },
    },
    actions: {
        async login({ commit }, data) {
            const { token } = await instance.post('/login', data);
            commit('setToken', token);
        },
        async register({ dispatch }, data) {
            await instance.post('/register', data);
            return dispatch('login', data);
        },
        async logout({ commit, state }) {
            await instance.post('/logout', { token: state.token });
            commit('setToken', null);
        },
    },
});
