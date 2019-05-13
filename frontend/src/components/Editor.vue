<template>
    <div>
        <codemirror :options="cmOptions" :value="sample" ref="cm" />
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
import Sample from '../assets/test';

export default {
    components: {
        codemirror,
    },
    watch: {
        theme(theme) {
            this.$refs.cm.codemirror.setOption('theme', theme);
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
                    // 当匹配只有一项的时候是否自动补全
                    completeSingle: false,
                },
                // 快捷键 可提供三种模式 sublime、emacs、vim
                keyMap: 'sublime',
                matchBrackets: true,
                showCursorWhenSelecting: true,
                theme: this.theme,
                extraKeys: { Ctrl: 'autocomplete' },
            },
            sample: Sample,
        };
    },
};
</script>

<style lang="scss">
    .CodeMirror {
        height: 70vh;
    }
</style>
