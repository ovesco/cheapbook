/* eslint-disable no-param-reassign */
import Vue from 'vue';
import Vuex from 'vuex';
import VuexPersist from 'vuex-persist';
import Axios from 'axios';

const instance = Axios.create({
    baseURL: 'http://localhost:9000',
});

Vue.use(Vuex);

const VuexLocal = new VuexPersist({
    storage: window.localStorage,
    reducer: state => ({ token: state.token }),
});

export default new Vuex.Store({
    plugins: [VuexLocal.plugin],
    state: {
        token: null,
        environments: [],
        env: null,
        deps: [],
        output: null,
        running: false,
    },
    mutations: {
        setToken(state, token) {
            state.token = token;
        },
        setEnvironments(state, envs) {
            state.environments.splice(0, state.environments.length);
            state.environments = envs;
        },
        addEnvironment(state, env) {
            state.envs.push(env);
        },
        selectEnvironment(state, env) {
            state.env = env;
        },
        updateCode(state, code) {
            state.env.code = code;
            state.environments.find(i => i.id === state.env.id).code = code;
        },
    },
    actions: {
        async login({ commit }, data) {
            const { data: { token } } = await instance.post('/login', data);
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
        async refreshEnvironments({ state, dispatch, commit }) {
            const { data: { envs } } = await instance.get(`/env/all?token=${state.token}`);
            commit('setEnvironments', envs);
            if (state.environments.length === 0) await dispatch('createEnvironment');
            if (state.env === null
                || state.environments.find(i => i.id === state.env.id) === undefined) {
                commit('selectEnvironment', state.environments[0]);
            }
        },
        async createEnvironment({ state, dispatch }) {
            await instance.post('/env', { token: state.token, code: '' });
            await dispatch('refreshEnvironments');
        },
        async deleteEnvironment({ state, dispatch }, id) {
            await instance.delete(`/env?token=${state.token}&id=${id}`);
            await dispatch('refreshEnvironments');
        },
        async updateCode({ state, commit }, code) {
            await instance.put('/env', { token: state.token, id: state.env.id, code });
            commit('updateCode', code);
        },
        async selectEnvironment({ state, commit }, id) {
            const env = state.environments.find(i => i.id === id);
            commit('selectEnvironment', env);
        },
        async refreshDependencies({ state }) {
            const { data: { deps } } = await instance.get(`/dependencies/all?token=${state.token}&envId=${state.env.id}`);
            state.deps = deps;
        },
        async addDependency({ state, dispatch }, dependency) {
            await instance.post('/dependencies', { token: state.token, envId: state.env.id, dependency });
            await dispatch('refreshDependencies');
        },
        async removeDependency({ state, dispatch }, id) {
            await instance.delete(`/dependencies?token=${state.token}&id=${id}`);
            await dispatch('refreshDependencies');
        },
        async run({ state }) {
            state.running = true;
            const { data } = await instance.post('/run', { token: state.token, envId: state.env.id });
            state.output = data;
            state.running = false;
        },
        async stop({ state }) {
            await instance.post('/stop', { token: state.token, envId: state.env.id });
            state.output = null;
            state.running = false;
        },
    },
});
