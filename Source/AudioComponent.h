/*
//=============================================================================

    AudioComponent.h
    Created: 12 Aug 2023 12:49:02pm
    Author:  Brendan D Bassett

//=============================================================================
*/

#pragma once

#include <JuceHeader.h>
//===================================================================================================================

/// @brief Displays chords in relative intonation.
class AudioComponent : public juce::Component, public juce::ChangeListener
{

public:
    //=========================================================================

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
    }

    ~AudioComponent() override
    {
    }

    //-------------------------------------------------------------------------

    void changeListenerCallback(juce::ChangeBroadcaster* source) override
    {
        if (source == &transportSource)
        {
            if (transportSource.isPlaying())
                changeState(Playing);
            else if ((state == Stopping) || (state == Playing))
                changeState(Stopped);
            else if (Pausing == state)
                changeState(Paused);
        }
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
    //=========================================================================

    enum TransportState
    {
        Paused,
        Pausing,
        Playing,
        Starting,
        Stopped,
        Stopping
    };

    //-------------------------------------------------------------------------

    void changeState(TransportState newState)
    {
        if (state != newState)
        {
            state = newState;

            switch (state)
            {
            case Paused:
                playButton.setButtonText("Resume");
                stopButton.setButtonText("Start Over");
                break;

            case Pausing:
                transportSource.stop();
                break;

            case Playing:
                playButton.setButtonText("Pause");
                stopButton.setButtonText("Stop");
                stopButton.setEnabled(true);
                break;

            case Starting:
                transportSource.start();
                break;

            case Stopped:
                playButton.setButtonText("Play");
                stopButton.setButtonText("Stop");
                stopButton.setEnabled(false);
                transportSource.setPosition(0.0);
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
            juce::File("C://Users/bbass/GitRepos/Prismus/Extras/Drones"),
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
        if ((state == Stopped) || (state == Paused))
            changeState(Starting);
        else if (state == Playing)
            changeState(Pausing);
    }

    void stopButtonClicked()
    {
        if (state == Paused)
            changeState(Stopped);
        else
            changeState(Stopping);
    }

    //-------------------------------------------------------------------------

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