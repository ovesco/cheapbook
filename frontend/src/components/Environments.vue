<template>
    <div>
        <a-modal @ok="handleCreate" :confirmLoading="confirm" @cancel="create = false"
                 :visible="create" title="Create a new environment">
            <p>Yoyoyoyo</p>
        </a-modal>
        <drawer title="Environments" :width="400"
                placement="right" :visible="show" @close="$emit('close')">
            <div>
                <div>
                    <a-button block type="dashed" class="new-env" @click="create = true">
                        New environment
                    </a-button>
                    <a-list bordered itemLayout="horizontal" :dataSource="environments">
                        <a-list-item slot="renderItem" slot-scope="item">
                            <a-list-item-meta>
                                <h3 slot="title">{{ item.name }}</h3>
                                <div slot="description" class="env-toolbar">
                                    <a-button v-if="item.id !== value" type="primary"
                                        @click="$emit('input', item.id)">
                                        Activate
                                    </a-button>
                                    <span v-else>Currently activated</span>
                                    <a-button type="danger">Delete</a-button>
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
    Modal,
} from 'ant-design-vue';

export default {
    components: {
        Drawer,
        aButton: Button,
        aModal: Modal,
        aList: List,
        aListItem: List.Item,
        aListItemMeta: List.Item.Meta,
    },
    data() {
        return {
            environments: [
                { id: 1, name: 'Mon super environnement', dependencies: 2 },
                { id: 2, name: 'Un autre incroyable environnement', dependencies: 47 },
            ],
            confirm: false,
            create: false,
        };
    },
    methods: {
        handleCreate() {
            this.confirm = true;
            setTimeout(() => {
                this.confirm = false;
                this.create = false;
            }, 2000);
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
