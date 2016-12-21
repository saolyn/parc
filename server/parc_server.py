#!/usr/bin/env python3.5

import socket
from argparse import ArgumentParser
from contextlib import contextmanager
from sys import stderr
from subprocess import run

import Quartz
from applescript import AppleScript, ScriptError


# NSEvent.h
NSSystemDefined = 14


# hidsystem/ev_keymap.h
NX_KEYTYPE_SOUND_UP = 0
NX_KEYTYPE_SOUND_DOWN = 1
NX_KEYTYPE_BRIGHTNESS_UP = 2
NX_KEYTYPE_BRIGHTNESS_DOWN = 3
NX_KEYTYPE_MUTE = 7
NX_KEYTYPE_PLAY = 16
NX_KEYTYPE_NEXT = 17
NX_KEYTYPE_PREVIOUS = 18
NX_KEYTYPE_ILLUMINATION_UP = 21
NX_KEYTYPE_ILLUMINATION_DOWN = 22
NX_KEYTYPE_ILLUMINATION_TOGGLE = 23


script = AppleScript('''
    on keynote_move(forward)
        if running of application "Keynote" is true then
            tell application "Keynote"
                activate
                try
                    if playing is true then
                        tell the front document
                            if forward then
                                show next
                            else
                                show previous
                            end if
                        end tell
                    end if
                end try
            end tell
        end if
    end keynote_move

    on keynote_start()
        if running of application "Keynote" is true then
            tell application "Keynote"
                activate
                try
                    if playing is false then start the front document from the first slide of the front document
                end try
            end tell
        end if
    end keynote_start
''')


def _do_key(key, down):
    ev = Quartz.NSEvent.otherEventWithType_location_modifierFlags_timestamp_windowNumber_context_subtype_data1_data2_(
        NSSystemDefined,  # type
        (0, 0),  # location
        0xa00 if down else 0xb00,  # flags
        0,  # timestamp
        0,  # window
        0,  # ctx
        8,  # subtype
        (key << 16) | ((0xa if down else 0xb) << 8),  # data1
        -1  # data2
    )
    cev = ev.CGEvent()
    Quartz.CGEventPost(0, cev)


def hid_post_aux_key(key):
    _do_key(key, down=True)
    _do_key(key, down=False)


@contextmanager
def _listen_socket(ip, port):
    sock = None
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.bind((ip, port))
        yield sock
    finally:
        if sock is not None:
            sock.close()


def run_server(ip, port, verbose):
    with _listen_socket(ip, port) as sock:
        print("PARC Server listening on: udp://{}:{}".format(ip, port))
        print("(Press ^C to terminate)")
        try:
            while True:
                raw_data, addr = sock.recvfrom(1024)  # buffer size is 1024 bytes
                data = raw_data.decode('UTF-8')
                if verbose: print("Processing: " + data)
                try:
                    if data == 'NOP':
                        pass
                    elif data == 'VOLU':
                        hid_post_aux_key(NX_KEYTYPE_SOUND_UP)
                    elif data == 'VOLD':
                        hid_post_aux_key(NX_KEYTYPE_SOUND_DOWN)
                    elif data == "MUTE":
                        hid_post_aux_key(NX_KEYTYPE_MUTE)
                    elif data == 'KNN':
                        script.call('keynote_move', True)
                    elif data == 'KNP':
                        script.call('keynote_move', False)
                    elif data == "KNS":
                        script.call('keynote_start')
                    elif data == 'LOCK':
                        run(("pmset", "displaysleepnow"))
                    elif data == 'MKPLAY':
                        hid_post_aux_key(NX_KEYTYPE_PLAY)
                    elif data == "MKNEXT":
                        hid_post_aux_key(NX_KEYTYPE_NEXT)
                    elif data == "MKPREV":
                        hid_post_aux_key(NX_KEYTYPE_PREVIOUS)
                    elif data == "BRIGHTU":
                        hid_post_aux_key(NX_KEYTYPE_BRIGHTNESS_UP)
                    elif data == "BRIGHTD":
                        hid_post_aux_key(NX_KEYTYPE_BRIGHTNESS_DOWN)
                    elif data == "KBBLU":
                        hid_post_aux_key(NX_KEYTYPE_ILLUMINATION_UP)
                    elif data == "KBBLD":
                        hid_post_aux_key(NX_KEYTYPE_ILLUMINATION_DOWN)
                    elif data == "KBBLT":
                        hid_post_aux_key(NX_KEYTYPE_ILLUMINATION_TOGGLE)
                    else:
                        print("received unknown message: {}.".format(data))
                except ScriptError as err:
                    print(err, file=stderr)
        except KeyboardInterrupt:
            print("exiting")
            return


if __name__ == '__main__':
    parser = ArgumentParser(description="PARC Server")
    parser.add_argument('-i', '--ip', default='0.0.0.0', help="IP Address to listen to")
    parser.add_argument('-p', '--port', type=int, default=8089, help="Port to listen on")
    parser.add_argument('-v', '--verbose', help="Show the received data.", action="store_true")

    args = parser.parse_args()

    run_server(args.ip, args.port, args.verbose)
