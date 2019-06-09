<template>
    <div>
        <!--
        <a-modal @ok="handleCreate" :confirmLoading="confirm" @cancel="create = false"
                 :visible="create" title="Create a new environment">
            <p>Yoyoyoyo</p>
        </a-modal>
        -->
        <drawer title="Environments" :width="400"
                placement="right" :visible="show" @close="$emit('close')">
            <div>
                <div>
                    <a-button block type="dashed" class="new-env" @click="handleCreate">
                        New environment
                    </a-button>
                    <a-list bordered itemLayout="horizontal"
                            :dataSource="$store.state.environments">
                        <a-list-item slot="renderItem" slot-scope="item">
                            <a-list-item-meta>
                                <h3 slot="title">{{ item.id }}</h3>
                                <div slot="description" class="env-toolbar">
                                    <a-button v-if="item.id !== $store.state.env.id" type="primary"
                                        @click="activate(item.id)">
                                        Activate
                                    </a-button>
                                    <span v-else>Currently activated</span>
                                    <a-button @click="remove(item.id)" type="danger">
                                        Delete
                                    </a-button>
                                </div>
                            </a-list-item-meta>
                        </a-list-item>
                    </a-list>
                </div>
            </div>
        </drawer>
    </div>
</template>

<script>
import {
    Drawer,
    Button,
    List,
    // Modal,
} from 'ant-design-vue';

export default {
    components: {
        Drawer,
        aButton: Button,
        // aModal: Modal,
        aList: List,
        aListItem: List.Item,
        aListItemMeta: List.Item.Meta,
    },
    async mounted() {
        await this.$store.dispatch('refreshEnvironments');
    },
    data() {
        return {
            confirm: false,
            create: false,
        };
    },
    methods: {
        async handleCreate() {
            this.confirm = true;
            await this.$store.dispatch('createEnvironment');
            this.confirm = false;
            this.create = false;
        },
        async remove(id) {
            await this.$store.dispatch('deleteEnvironment', id);
        },
        async activate(id) {
            await this.$store.dispatch('selectEnvironment', id);
            this.$emit('close');
        },
    },
    props: ['value', 'show'],
};
</script>

<style lang="scss">
    .env-toolbar {

        * {
            margin-right: 0.5rem;
        }
    }

    .new-env {
        margin-bottom: 1rem;
    }
</style>
