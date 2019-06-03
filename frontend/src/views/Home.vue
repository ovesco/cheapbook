<template>
    <div>
        <div class="home">
            <div class="chooser">
                <img src="../assets/images/kamil.png" alt="kamil" />
                <h1>Cheapbook</h1>
                <h3>A cheap Jupyter notebook alternative</h3>
                <a-form class="form" :form="form" @submit="submit">
                    <a-form-item>
                        <a-input placeholder="Username" v-decorator="['username']">
                            <a-icon slot="prefix" type="user" />
                        </a-input>
                    </a-form-item>
                    <a-form-item>
                        <a-input placeholder="Password" v-decorator="['password']">
                            <a-icon slot="prefix" type="unlock" />
                        </a-input>
                    </a-form-item>
                    <a-form-item v-if="register">
                        <a-input v-decorator="['password2']"
                                 placeholder="Confirm password">
                            <a-icon slot="prefix" type="unlock" />
                        </a-input>
                    </a-form-item>
                    <a-button block type="dashed" html-type="submit">
                        {{ register ? 'Register to' : 'Enter' }} the realm
                    </a-button>
                </a-form>
                <a class="account">
                    <span v-if="register" @click="register = false">I already got an account</span>
                    <span v-else @click="register = true">I got no account pls</span>
                </a>
            </div>
        </div>
    </div>
</template>

<script>
import {
    Button,
    Input,
    Icon,
    Form,
} from 'ant-design-vue';

export default {
    name: 'home',
    components: {
        aButton: Button,
        aInput: Input,
        aIcon: Icon,
        aForm: Form,
        aFormItem: Form.Item,
    },
    data() {
        return {
            register: false,
            form: this.$form.createForm(this),
        };
    },
    methods: {
        async submit(e) {
            e.preventDefault();
            await this.form.validateFields(async (err, values) => {
                const { username, password, password2 } = values;
                if (this.register) {
                    if (password !== password2) console.log('Password not the same yo');
                    else await this.$store.dispatch('register', { username, password });
                } else await this.$store.dispatch('login', { username, password });
            });
        },
    },
};
</script>

<style scoped lang="scss">
    .home {
        width: 100%;
        height:80vh;
        display: flex;
        justify-content: center;
        align-items: center;

        .chooser {
            width:300px;
            text-align: center;

            img {
                width: 80px;
                display:block;
                margin:0 auto 1rem auto;
            }

            h1 {
                margin: 0;
            }

            h3 {
                margin-bottom: 1rem;
            }

            .account {
                display: block;
                padding-top:0.8rem;
                font-size:0.7rem;
            }

            .form {

                width:250px;
                margin:auto;

                > * {
                    margin-bottom: 0.5rem;
                }
            }
        }
    }
</style>
