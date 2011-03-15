package net.smartsocket.clients;

/**
 * Later on, when UDPClients become available, this class will have more effect.
 * Right now, this class is pretty much used as a base class for a few calls.
 * Pretty much disregard this skeleton class for now.
 * @author XaeroDegreaz
 */
public class AbstractClient extends Thread {
    /**
     * Some sort of unique identifier to assign this client.
     */
    private Object uniqueId;

    public void AbstractClient() {
        
    }

    @Override
    public void run() {
    }

    /**
     * Some sort of unique identifier to assign this client.
     * @return the uniqueId
     */
    public Object getUniqueId() {
        return uniqueId;
    }

    /**
     * Some sort of unique identifier to assign this client.
     * @param uniqueId the uniqueId to set
     */
    public void setUniqueId(Object uniqueId) {
        this.uniqueId = uniqueId;
    }

}
