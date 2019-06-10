<template>
    <div>
        <a-modal @ok="handleAdd" :confirmLoading="confirm" @cancel="adding = false"
                 :visible="adding" title="Add a dependency">
            <div>
                Dependencies must come from the <a href="https://mvnrepository.com">Maven repositories</a>
                and be included in SBT format, for example
                <br/>
                <div class="code">
                    libraryDependencies += "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.2"
                </div>
            </div>
            <a-input v-model="dependance" placeholder="Dependency line" />
        </a-modal>
        <drawer title="Environments" :width="400"
                placement="right" :visible="show" @close="$emit('close')">
            <div>
                <div>
                    <a-button block type="dashed" class="new-env" @click="adding = true">
                        Add a dependency
                    </a-button>
                    <a-list bordered itemLayout="horizontal"
                            :dataSource="$store.state.deps">
                        <a-list-item slot="renderItem" slot-scope="item">
                            <a-list-item-meta>
                                <h3 slot="title">{{ item.dependency }}</h3>
                                <div slot="description" class="env-toolbar">
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
    Modal,
    Input,
    List,
    Drawer,
    Button,
} from 'ant-design-vue';

export default {
    components: {
        aModal: Modal,
        aInput: Input,
        aList: List,
        aListItem: List.Item,
        aListItemMeta: List.Item.Meta,
        drawer: Drawer,
        aButton: Button,
    },
    async mounted() {
        await this.$store.dispatch('refreshDependencies');
    },
    data() {
        return {
            confirm: false,
            dependencies: null,
            dependance: null,
            adding: false,
        };
    },
    methods: {
        async handleAdd() {
            this.confirm = true;
            await this.$store.dispatch('addDependency', this.dependance);
            this.dependance = '';
            this.confirm = false;
            this.adding = false;
        },
        async remove(id) {
            await this.$store.dispatch('removeDependency', id);
        },
    },
    props: ['show'],
};
</script>

<style scoped>
   .code {
        background: #eee;
        font-size:0.8rem;
        padding:0.5rem;
       margin:0.5rem 0;
        border-radius:5px;
    }
</style>
