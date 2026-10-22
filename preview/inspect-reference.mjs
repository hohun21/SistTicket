import fs from 'node:fs/promises';
import {PresentationFile,FileBlob} from 'file:///C:/Users/a0107/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/@oai/artifact-tool/dist/artifact_tool.mjs';
const p=await PresentationFile.importPptx(await FileBlob.load('C:/Users/a0107/OneDrive/문서/카카오톡 받은 파일/1조_오늘의집클론_조지훈_문규리_류호훈_안정빈_장미성/1조_오늘의집_클론.pptx'));
await fs.mkdir('preview/reference',{recursive:true});
console.log('slides',p.slides.items.length);
await fs.writeFile('preview/reference/inspect.json',JSON.stringify(await p.inspect({kind:'slide,textbox,shape,image,layout',maxChars:150000})));
for(let i=0;i<p.slides.items.length;i++){
const b=await p.export({slide:p.slides.items[i],format:'png',scale:0.6});
await fs.writeFile(`preview/reference/slide-${i+1}.png`,new Uint8Array(await b.arrayBuffer()));
console.log('rendered',i+1);
}
