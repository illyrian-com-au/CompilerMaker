package au.com.illyrian.classmaker.proxy;

import junit.framework.TestCase;

import org.junit.Test;

public class ActorProxyTest extends TestCase {
    static interface MessageActor {
        void message0();
        
        void message1(String msg);
        
        void message2(String msg, Integer value);
    }
    
    static class MessageActorImpl implements MessageActor {
        private boolean hasMessage = false;
        private String msg = null;
        private Integer value = null;

        @Override
        public void message0() {
            hasMessage = true;
        }

        @Override
        public void message1(String msg) {
            hasMessage = true;
            this.msg = msg;
        }

        @Override
        public void message2(String msg, Integer value) {
            hasMessage = true;
            this.msg = msg;
            this.value = value;
        }

        public boolean hasMessage() {
            return hasMessage;
        }

        public String getMsg() {
            return msg;
        }

        public Integer getValue() {
            return value;
        }
    }
    
    @Test
    public void testMessageActor() {
        MessageActorImpl actor = new MessageActorImpl();
        actor.message0();
        assertTrue("hasMessage", actor.hasMessage());
        actor.message1("Hello world");
        assertEquals("getMessage", "Hello world", actor.getMsg());
        actor.message2("Hello world", 42);
        assertEquals("getValue", new Integer(42), actor.getValue());
    }
}
