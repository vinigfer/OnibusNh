package br.com.solenoide.empresas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Empresa {
    protected Map<String, String> linhas = new HashMap<String, String>();
    protected Map<String, String> dias = new HashMap<String, String>();
    protected Map<String, String> sentido = new HashMap<String, String>();

    public String getIdDoDiaUsandoNome(String valor) {
        for (String id : dias.keySet()) {
            String value = dias.get(id);
            if (value.equals(valor))
                return id;
        }
        return "";
    }

    public String getIdDaLinhaUsandoNome(String nome) {
        for (String id : linhas.keySet()) {
            String value = linhas.get(id);
            if (value.equals(nome))
                return id;
        }
        return "";
    }

    public Set<String> getIdsDasLinhas() {
        return linhas.keySet();
    }

    public Set<String> getIdsDosDias() {
        return dias.keySet();
    }

    public String[] getNomeDosDiasAsStringArray() {
        List<String> list = new ArrayList<String>();
        for (String linha : dias.values())
            list.add(linha);
        return list.toArray(new String[list.size()]);
    }

    public String[] getNomeDasLinhasAsStringArray() {
        List<String> list = new ArrayList<String>();
        for (String linha : linhas.values())
            list.add(linha);
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }
}