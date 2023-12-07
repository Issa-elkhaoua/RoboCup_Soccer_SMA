/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Arthur Lefebvre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package fstt.sim.modele;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.stream.Stream;

public abstract class MessagesHelper {
    public static ACLMessage createBallonQuery(AID receiverId){
        ACLMessage demandeBallon = new ACLMessage(ACLMessage.QUERY_IF);
        demandeBallon.setOntology(MessagesConstantes.ONTOLOGY_BALLON);
        demandeBallon.addReceiver(receiverId);//new AID("handler.getTerrain()", AID.ISLOCALNAME)
        demandeBallon.setContent(MessagesConstantes.QUERY_BALLON);
        return demandeBallon;
    }

    public static ACLMessage createDebutDuMatchRequest(Stream<AID> joueursIds){
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setOntology(MessagesConstantes.ONTOLOGY_TEMPS);
        joueursIds.forEach(msg::addReceiver);
        msg.setContent(MessagesConstantes.REQUEST_DEBUT_MATCH);
        return msg;
    }

    public static ACLMessage createFinDuMatchRequest(Stream<AID> joueursIds){
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setOntology(MessagesConstantes.ONTOLOGY_TEMPS);
        joueursIds.forEach(msg::addReceiver);
        msg.setContent(MessagesConstantes.REQUEST_FIN_MATCH);
        return msg;
    }
}
