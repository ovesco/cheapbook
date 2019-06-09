<template>
    <div>
        <codemirror :options="cmOptions" v-model="code" ref="cm" />
    </div>
</template>

<script>
import { codemirror } from 'vue-codemirror';
// language
import 'codemirror/lib/codemirror.css';
// theme css
import 'codemirror/theme/base16-light.css';
import 'codemirror/theme/monokai.css';
import 'codemirror/theme/paraiso-light.css';
import 'codemirror/theme/rubyblue.css';
// require active-line
import 'codemirror/addon/selection/active-line';
// styleSelectedText
import 'codemirror/addon/selection/mark-selection';
import 'codemirror/addon/search/searchcursor';

// keyMap
import 'codemirror/mode/clike/clike';
import 'codemirror/addon/edit/matchbrackets';
import 'codemirror/addon/comment/comment';
import 'codemirror/addon/dialog/dialog';
import 'codemirror/addon/dialog/dialog.css';

import 'codemirror/addon/search/search';
import 'codemirror/keymap/sublime';
// foldGutter
import 'codemirror/addon/fold/foldgutter.css';
import 'codemirror/addon/fold/brace-fold';
import 'codemirror/addon/fold/comment-fold';
import 'codemirror/addon/fold/foldcode';
import 'codemirror/addon/fold/foldgutter';
import 'codemirror/addon/fold/indent-fold';
import 'codemirror/addon/fold/markdown-fold';
import 'codemirror/addon/fold/xml-fold';

export default {
    components: {
        codemirror,
    },
    mounted() {
        this.update();
    },
    watch: {
        theme(theme) {
            this.$refs.cm.codemirror.setOption('theme', theme);
        },
        env() {
            this.update();
        },
        code(newCode) {
            if (!this.env) return;
            if (this.timer !== null) clearTimeout(this.timer);
            this.timer = setTimeout(() => {
                this.$store.dispatch('updateCode', newCode).then(() => {
                    this.timer = null;
                });
            }, 1000);
        },
    },
    methods: {
        update() {
            const item = this.env;
            if (item === null || item === undefined) this.code = 'No environment selected';
            else this.code = item.code;
        },
    },
    computed: {
        env() {
            return this.$store.state.env;
        },
    },
    props: ['theme'],
    data() {
        return {
            cmOptions: {
                tabSize: 4,
                styleActiveLine: false,
                lineNumbers: true,
                styleSelectedText: false,
                line: true,
                foldGutter: true,
                gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter'],
                highlightSelectionMatches: {
                    showToken: /\w/,
                    annotateScrollbar: true,
                },
                mode: 'text/x-scala',
                // hint.js options
                hintOptions: {
                    completeSingle: false,
                },
                keyMap: 'sublime',
                matchBrackets: true,
                showCursorWhenSelecting: true,
                theme: this.theme,
                extraKeys: { Ctrl: 'autocomplete' },
            },
            code: '',
            timer: null,
        };
    },
};
</script>

<style lang="scss">
    .CodeMirror {
        height: 70vh;
    }
</style>
