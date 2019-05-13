<template>
    <div>
        <div class="home">
            <div class="chooser">
                <img src="../assets/images/kamil.png" alt="kamil" />
                <h1>Cheapbook</h1>
                <h4>A cheap Jupyter notebook alternative</h4>
                <router-link to="editor">
                    <a-button type="dashed">Enter the realm</a-button>
                </router-link>
            </div>
        </div>
    </div>
</template>

<script>
import { Button } from 'ant-design-vue';

export default {
    name: 'home',
    components: {
        aButton: Button,
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

            h4 {
                margin-bottom: 1rem;
            }
        }
    }
</style>
