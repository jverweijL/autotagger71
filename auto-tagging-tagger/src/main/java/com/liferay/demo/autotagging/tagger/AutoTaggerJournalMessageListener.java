package com.liferay.demo.autotagging.tagger;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import org.osgi.service.component.annotations.Component;

@Component(
        immediate=true,property=("destination.name=Autotagger"),
        service = MessageListener.class
)
public class AutoTaggerJournalMessageListener implements MessageListener {

    @Override
    public void receive(Message message) throws MessageListenerException {
// and process message
        _log.debug("AutoTaggerJournalMessageListener Received a message");
        _log.debug("Let's wait a second or two");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        _log.debug("What are you telling me...process journal " + message.get("key"));
    }

    private static final Log _log = LogFactoryUtil.getLog(
            AutoTaggerJournalMessageListener.class);
}
