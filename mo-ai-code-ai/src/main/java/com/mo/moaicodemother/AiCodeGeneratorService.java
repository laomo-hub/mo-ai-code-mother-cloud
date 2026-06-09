package com.mo.moaicodemother;

import com.mo.moaicodemother.model.HtmlCodeResult;
import com.mo.moaicodemother.model.MultiFileCodeResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {

    /**
     * 生成代码
     * @param userMassage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMassage);

    /**
     * 生成代码
     * @param userMassage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMassage);

    /**
     * 生成单Html代码
     * @param userMassage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(String userMassage);

    /**
     * 生成多文件代码
     * @param userMassage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMultiFileCodeStream(String userMassage);

    /**
     * 生成Vue代码
     * @param userMassage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-vue-project-system-prompt")
    TokenStream generateVueProjectCodeStream(@MemoryId long appId, @UserMessage String userMassage);
}
