#!/usr/bin/env perl

use strict;
use warnings;
use XML::Parser::PerlSAX;

my $handler = MyHandler->new();
my $parser  = XML::Parser::PerlSAX->new( Handler => $handler );
my $file    = $ARGV[0];

my %parser_args = ( Source => { SystemId => $file } );
$parser->parse(%parser_args);

exit;

package MyHandler;
use strict;
use warnings;
use Encode;

my @strings;
my @stash;
my @output_elements;
my $do_output;
my $page_element;

sub new {
    my $type = shift;
    @output_elements = qw/title text/;
    $page_element    = 'page';
    return bless {}, $type;
}

sub start_element {
    my ( $self, $element ) = @_;

    if ( grep { $element->{Name} eq $_ } @output_elements ) {
        $do_output = 1;
    }
}

sub characters {
    my ( $self, $characters ) = @_;
    my $text = $characters->{Data};
    if ($do_output) {
        push @strings, $text;
    }
}

sub end_element {
    my ( $self, $element ) = @_;
    if ( grep { $element->{Name} eq $_ } @output_elements ) {
        my $text = encode( 'utf-8', join( '', @strings ) );
        $text =~ tr/\t\r\n/ /;
        push @stash, $text;
        @strings   = ();
        $do_output = 0;
    }
    elsif ( $element->{Name} eq $page_element ) {
        my $text = join( "\t", @stash );
        print $text, "\n";
        @stash = ();
    }
}

sub start_document {
    my ($self) = @_;
}

sub end_document {
    my ($self) = @_;
}

1;
