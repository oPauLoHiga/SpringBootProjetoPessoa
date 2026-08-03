package com.empresa.cadrastro_pessoas.sugestao;

public enum StatusSugestao {
    // Ordem do fluxo de vida de uma sugestão:
    PENDENTE,    // recém criada, aguardando análise
    ANALISANDO,  // em processo de avaliação
    APROVADA,    // aprovada para implementação
    REJEITADA    // descartada com justificativa
}
