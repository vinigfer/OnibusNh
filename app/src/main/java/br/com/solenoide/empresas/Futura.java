package br.com.solenoide.empresas;

import java.util.Set;

public class Futura extends Empresa {

    public Futura() {
        linhas.put("90", "BOA SAUDE ESCOLA LIBERATO");
        linhas.put("52", "BOA SAUDE JARDIM LIBERATO");
        linhas.put("53", "BOA SAUDE OSVALDO CRUZ");
        linhas.put("76", "BOA SAUDE PARQUE LIBERATO");
        linhas.put("87", "BOA SAUDE RESIDENCIAL NH VILA TORRES");
        linhas.put("70", "BOA SAUDE RODOVIÁRIA");
        linhas.put("56", "BRIGADA");
        linhas.put("89", "CIRCULAR  FENAC  RODOVIÁRIA");
        linhas.put("58", "CIRCULAR  OSVALDO CRUZ");
        linhas.put("54", "CIRCULAR FENAC");
        linhas.put("55", "CIRCULAR IDEAL");
        linhas.put("51", "CIRCULAR PETRÓPOLIS");
        linhas.put("73", "CIRCULAR PETROPOLIS RODOVIÁRIA");
        linhas.put("88", "ESMERALDA GUIA LOPES  BOA  SAUDE");
        linhas.put("61", "GUIA LOPES ESMERALDA");
        linhas.put("81", "GUIA LOPES ESMERALDA RUA CONSTITUINTE");
        linhas.put("74", "IDEAL BOA VISTA SELETIVO");
        linhas.put("59", "OSVALDO CRUZ ESCOLA LIBERATO");
        linhas.put("72", "OSVALDO CRUZ RODOVIÁRIA");
        linhas.put("60", "PAQUISTÃO");
        linhas.put("71", "RES. PRINCESA ISABEL  VIA RES. VALE DAS FIGUEIRAS");
        linhas.put("69", "RINCÃO");
        linhas.put("65", "RONDONIA ESMERALDA");
        linhas.put("91", "RONDONIA V.K ESCOLA LIBERATO");
        linhas.put("64", "TRAVESSÃO ESMERALDA");
        linhas.put("62", "TRENZINHO");
        linhas.put("66", "VILA  KROEFF  ESMERALDA");
        linhas.put("68", "VILA KROEFF RONDONIA");
        linhas.put("57", "VILA KROEFF SANTA CLARA");
        linhas.put("67", "VILA TORRES");

        dias.put("0", "Dias Úteis");
        dias.put("1", "Sábados");
        dias.put("2", "Domingos e feriados");

        sentido.put("0", "Bairro - Centro");
        sentido.put("1", "Centro - Bairro");
    }

    public Set<String> getIdsDosSentidos() {
        return sentido.keySet();
    }

    public String getValueDoSentidoUsandoNome(String key) {
        return sentido.get(key);
    }
}
