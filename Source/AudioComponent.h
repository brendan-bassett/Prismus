/*
//===================================================================================================================

    AudioComponent.h
    Created: 12 Aug 2023 12:49:02pm
    Author:  Brendan Bassett

//===================================================================================================================
*/

#pragma once


//===================================================================================================================
/* CLASS: AudioComponent
*/
class AudioComponent  : public juce::AudioAppComponent, public juce::ChangeListener
{

public:
    //===============================================================================================================

    AudioComponent() : state(Stopped)
    {
        levelSlider.setRange(0.0, 0.05);
        levelSlider.setTextBoxStyle(juce::Slider::TextBoxRight, false, 40, 20);
        levelLabel.setText("Noise Level", juce::dontSendNotification);

        addAndMakeVisible(levelSlider);
        addAndMakeVisible(levelLabel);

        addAndMakeVisible(&openButton);
        openButton.setButtonText("Open...");
        openButton.onClick = [this] { openButtonClicked(); };

        addAndMakeVisible(&playButton);
        playButton.setButtonText("Play");
        playButton.onClick = [this] { playButtonClicked(); };
        playButton.setColour(juce::TextButton::buttonColourId, juce::Colours::green);
        playButton.setEnabled(false);

        addAndMakeVisible(&stopButton);
        stopButton.setButtonText("Stop");
        stopButton.onClick = [this] { stopButtonClicked(); };
        stopButton.setColour(juce::TextButton::buttonColourId, juce::Colours::red);
        stopButton.setEnabled(false);

        formatManager.registerBasicFormats();
        transportSource.addChangeListener(this);

        setAudioChannels(0, 2);
    }

    ~AudioComponent() override
    {
        shutdownAudio();
    }

    //---------------------------------------------------------------------------------------------------------------

    void changeListenerCallback(juce::ChangeBroadcaster* source) override
    {
        if (source == &transportSource)
        {
            if (transportSource.isPlaying())
                changeState(Playing);
            else
                changeState(Stopped);
        }
    }

    void getNextAudioBlock(const juce::AudioSourceChannelInfo& bufferToFill) override
    {

        if (readerSource.get() == nullptr)
        {
            bufferToFill.clearActiveBufferRegion();
            return;
        }

        transportSource.getNextAudioBlock(bufferToFill);

        /*
        * LIVE AUDIO I/O
        * 
        auto* device = deviceManager.getCurrentAudioDevice();
        auto activeInputChannels = device->getActiveInputChannels();
        auto activeOutputChannels = device->getActiveOutputChannels();
        //! [getNextAudioBlock]

        //! [getNextAudioBlock 2]
        auto maxInputChannels = activeInputChannels.getHighestBit() + 1;
        auto maxOutputChannels = activeOutputChannels.getHighestBit() + 1;
        //! [getNextAudioBlock 2]

        //! [getNextAudioBlock 3]
        auto level = (float)levelSlider.getValue();

        for (auto channel = 0; channel < maxOutputChannels; ++channel)
        {
            if ((!activeOutputChannels[channel]) || maxInputChannels == 0)
            {
                bufferToFill.buffer->clear(channel, bufferToFill.startSample, bufferToFill.numSamples);
            }
            //! [getNextAudioBlock 3]

            //! [getNextAudioBlock 4]
            else
            {
                auto actualInputChannel = channel % maxInputChannels; // [1]

                if (!activeInputChannels[channel]) // [2]
                {
                    bufferToFill.buffer->clear(channel, bufferToFill.startSample, bufferToFill.numSamples);
                }
                else // [3]
                {
                    auto* inBuffer = bufferToFill.buffer->getReadPointer(actualInputChannel,
                        bufferToFill.startSample);
                    auto* outBuffer = bufferToFill.buffer->getWritePointer(channel, bufferToFill.startSample);

                    for (auto sample = 0; sample < bufferToFill.numSamples; ++sample)
                    {
                        auto noise = (random.nextFloat() * 2.0f) - 1.0f;
                        outBuffer[sample] = inBuffer[sample] + (inBuffer[sample] * noise * level);
                    }
                }
            }
        }
        */
    }

    void prepareToPlay(int samplesPerBlockExpected, double sampleRate) override
    {
        transportSource.prepareToPlay(samplesPerBlockExpected, sampleRate);
    }

    void releaseResources() override
    {
        transportSource.releaseResources();
    }

    void resized() override
    {
        levelLabel.setBounds(0, 10, 70, 20);
        levelSlider.setBounds(80, 10, getWidth() - 90, 20);

        openButton.setBounds(10, 110, getWidth() - 20, 20);
        playButton.setBounds(10, 140, getWidth() - 20, 20);
        stopButton.setBounds(10, 170, getWidth() - 20, 20);
    }

private:
    //================================================================================================================

    enum TransportState
    {
        Stopped,
        Starting,
        Playing,
        Stopping
    };

    //-----------------------------------------------------------------------------------------------------------------

    void changeState(TransportState newState)
    {
        if (state != newState)
        {
            state = newState;

            switch (state)
            {
            case Stopped:
                stopButton.setEnabled(false);
                playButton.setEnabled(true);
                transportSource.setPosition(0.0);
                break;

            case Starting:
                playButton.setEnabled(false);
                transportSource.start();
                break;

            case Playing:
                stopButton.setEnabled(true);
                break;

            case Stopping:
                transportSource.stop();
                break;
            }
        }
    }

    void openButtonClicked()
    {
        chooser = std::make_unique<juce::FileChooser>("Select an mp3 file to play...",
            juce::File{"C:/Users/bbass/GitRepos/Prismus/Extras/Drones"},
            "*.mp3");
        auto chooserFlags = juce::FileBrowserComponent::openMode
            | juce::FileBrowserComponent::canSelectFiles;

        chooser->launchAsync(chooserFlags, [this](const juce::FileChooser& fc)
            {
                auto file = fc.getResult();

                if (file != juce::File{})
                {
                    auto* reader = formatManager.createReaderFor(file);

                    if (reader != nullptr)
                    {
                        auto newSource = std::make_unique<juce::AudioFormatReaderSource>(reader, true);
                        transportSource.setSource(newSource.get(), 0, nullptr, reader->sampleRate);
                        playButton.setEnabled(true);
                        readerSource.reset(newSource.release());
                    }
                }
            });
    }

    void playButtonClicked()
    {
        changeState(Starting);
    }

    void stopButtonClicked()
    {
        changeState(Stopping);
    }

    //--------------------------------------------------------------------------------------------------------------

    juce::Random random;
    juce::Slider levelSlider;
    juce::Label levelLabel;

    juce::TextButton openButton;
    juce::TextButton playButton;
    juce::TextButton stopButton;

    juce::AudioFormatManager formatManager;
    juce::AudioTransportSource transportSource;
    TransportState state;

    std::unique_ptr<juce::FileChooser> chooser;
    std::unique_ptr<juce::AudioFormatReaderSource> readerSource;

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(AudioComponent)
};
