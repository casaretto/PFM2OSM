/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mptoosm.elementosMapa;

import java.util.ArrayList;

/**
 * Esta classe modela a lista de numerações de uma via (way).
 * Ela é basicamente uma ArrayList de Numeracao contendo funcionalidades extras.
 *
 * @author Paulo Carvalho paulo.r.m.carvalho@gmail.com
 */
public class Numeracoes {

    public ArrayList<Numeracao> listaNumeracoes = new ArrayList<Numeracao>();

    public void addNumeracao( Numeracao num ){
        this.appendDistalNodeSeqInPoly( num.base_node_seq_in_poly );
        this.listaNumeracoes.add(num);
    }

    public boolean isVazia(){
        return this.listaNumeracoes.isEmpty();
    }

    public void limpar(){
        this.listaNumeracoes.clear();
    }

    /**
     * Define o sequencial (na polyline) do nó de roteamento (conecta 2+ vias ou é terminal) distal de
     * forma a completar a última numeração adicionada à lista.
     * Este método deve ser chamado idealmente antes de adicionar uma nova Numeração.
     * Isto é necessário porque no PFM as variaves NumberNN=... não contêm o sequencial do nó distal, só o base (inicial).
     * Normalmente o nó distal de uma Numeração N é o nó base de uma Numeração N+1.
     * Esquema:
     * [NO_BASE]---->----[*]---->----[*]-->-----[NO_DISTAL]  A seta é o sentido de desenho da polyline
     *    20                                         30
     * Os [*] são nós comuns (não de roteamento) que só definem a forma da via e não podem ter número.
     */
    public void appendDistalNodeSeqInPoly( String value ){
        if( !this.listaNumeracoes.isEmpty() ){
            Numeracao ult_num = this.listaNumeracoes.get( this.listaNumeracoes.size() - 1 );
            ult_num.setDistalNodeSeqInPoly( value );
        }
    }

    public Iterable<Numeracao> lista() {
        return this.listaNumeracoes;
    }

    public void makeNodes(){
        
    }
}
